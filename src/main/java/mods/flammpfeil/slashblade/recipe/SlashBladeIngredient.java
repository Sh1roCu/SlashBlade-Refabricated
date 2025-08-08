package mods.flammpfeil.slashblade.recipe;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashBladeIngredient implements CustomIngredient {
    private final Set<Item> items;
    private final RequestDefinition request;

    protected SlashBladeIngredient(Set<Item> items, RequestDefinition request) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SlashBladeIngredient with no items");
        }
        this.items = Collections.unmodifiableSet(items);
        this.request = request;
    }

    public static SlashBladeIngredient of(ItemLike item, RequestDefinition request) {
        return new SlashBladeIngredient(Set.of(item.asItem()), request);
    }

    public static SlashBladeIngredient of(RequestDefinition request) {
        return new SlashBladeIngredient(Set.of(SBItems.slashblade), request);
    }

    @Override
    public boolean test(ItemStack input) {
        if (input == null)
            return false;
        return items.contains(input.getItem()) && this.request.test(input);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return items.stream().map(item -> {
            ItemStack stack = new ItemStack(item);
            // copy NBT to prevent the stack from modifying the original, as capabilities or
            // vanilla item durability will modify the tag
            request.initItemStack(stack);
            // return new Ingredient.ItemValue(stack);
            return stack;
        }).toList();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

/*    @Override
    public boolean isSimple() {
        return false;
    }*/

    @Override
    public CustomIngredientSerializer<? extends CustomIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements CustomIngredientSerializer<SlashBladeIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation RES = SlashBlade.prefix("blade");

        @Override
        public ResourceLocation getIdentifier() {
            return RES;
        }

        @Override
        public SlashBladeIngredient read(JsonObject jsonObject) {
            // parse items
            Set<Item> items;
            if (jsonObject.has("item")) {
                ResourceLocation loc = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "item"));
                if (loc == null) {
                    throw new JsonSyntaxException("Item " + loc + " is not a valid item");
                }
                items = Set.of(BuiltInRegistries.ITEM.get(loc));
            } else if (jsonObject.has("items")) {
                ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
                JsonArray itemArray = GsonHelper.getAsJsonArray(jsonObject, "items");
                for (int i = 0; i < itemArray.size(); i++) {
                    ResourceLocation loc = ResourceLocation.tryParse(GsonHelper.convertToString(itemArray.get(i), "items[" + i + ']'));
                    if (loc == null) {
                        throw new JsonSyntaxException("Item " + loc + " is not a valid item");
                    }
                    builder.add(BuiltInRegistries.ITEM.get(loc));
                }
                items = builder.build();
            } else
                throw new JsonSyntaxException("Must set either 'item' or 'items'");
            var request = RequestDefinition.fromJSON(jsonObject.getAsJsonObject("request"));
            return new SlashBladeIngredient(items, request);
        }

        @Override
        public void write(JsonObject jsonObject, SlashBladeIngredient slashBladeIngredient) {
            jsonObject.addProperty("type", RES.toString());
            if (slashBladeIngredient.items.size() == 1) {
                jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(slashBladeIngredient.items.iterator().next()).toString());
            } else {
                JsonArray items = new JsonArray();
                // ensure the order of items in the set is deterministic when saved to JSON
                slashBladeIngredient.items.stream().map(BuiltInRegistries.ITEM::getKey).sorted().forEach(name -> items.add(name.toString()));
                jsonObject.add("items", items);
            }
            jsonObject.add("request", slashBladeIngredient.request.toJson());
        }

        @Override
        public SlashBladeIngredient read(FriendlyByteBuf friendlyByteBuf) {
            Set<Item> items = Stream.generate(() -> friendlyByteBuf.readById(BuiltInRegistries.ITEM))
                    .limit(friendlyByteBuf.readVarInt()).collect(Collectors.toSet());
            RequestDefinition request = RequestDefinition.fromNetwork(friendlyByteBuf);
            return new SlashBladeIngredient(items, request);
        }

        @Override
        public void write(FriendlyByteBuf buffer, SlashBladeIngredient ingredient) {
            buffer.writeVarInt(ingredient.items.size());
            for (Item item : ingredient.items)
                buffer.writeId(BuiltInRegistries.ITEM, item);
            ingredient.request.toNetwork(buffer);
        }
    }
}
