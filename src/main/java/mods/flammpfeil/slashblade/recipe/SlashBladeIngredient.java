package mods.flammpfeil.slashblade.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

import java.util.Set;
import java.util.stream.Stream;

public record SlashBladeIngredient(HolderSet<Item> holdItems, RequestDefinition request) implements CustomIngredient {
    public static final Codec<HolderSet<Item>> ITEM_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false);

    public static final MapCodec<SlashBladeIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ITEM_HOLDER_SET_CODEC.fieldOf("items").forGetter(ingredient -> ingredient.holdItems),
            RequestDefinition.CODEC.fieldOf("request").forGetter(ingredient -> ingredient.request)
    ).apply(builder, SlashBladeIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlashBladeIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(ITEM_HOLDER_SET_CODEC),
            ingredient -> ingredient.holdItems,
            RequestDefinition.STREAM_CODEC,
            ingredient -> ingredient.request,
            SlashBladeIngredient::new
    );


    public SlashBladeIngredient {
        if (holdItems.size() <= 0) {
            throw new IllegalArgumentException("Cannot create a SlashBladeIngredient with no items");
        }
    }

    private SlashBladeIngredient(Set<Item> items, RequestDefinition request) {
        this(HolderSet.direct(items.stream().map(Item::builtInRegistryHolder).toList()), request);
    }

    public static SlashBladeIngredient of(ItemLike item, RequestDefinition request) {
        return new SlashBladeIngredient(Set.of(item.asItem()), request);
    }

    public static SlashBladeIngredient of(RequestDefinition request) {
        return new SlashBladeIngredient(Set.of(SBItems.SLASHBLADE), request);
    }

    public static SlashBladeIngredient of(ItemLike item, Identifier request) {
        return new SlashBladeIngredient(Set.of(item.asItem()),
                RequestDefinition.Builder.newInstance().name(request).build());
    }

    public static SlashBladeIngredient of(Identifier request) {
        return new SlashBladeIngredient(Set.of(SBItems.SLASHBLADE),
                RequestDefinition.Builder.newInstance().name(request).build());
    }

    public static SlashBladeIngredient blankNameless() {
        return of(RequestDefinition.Builder.newInstance().build());
    }

    @Override
    public boolean test(ItemStack input) {
        if (input == null)
            return false;
        return holdItems.contains(input.getItem().builtInRegistryHolder()) && this.request.test(input);
    }

    @Override
    public Stream<Holder<Item>> items() {
        return holdItems.stream();
    }

    @Override
    public SlotDisplay display() {
        return new SlotDisplay.Composite(this.items().<SlotDisplay>map(item -> {
            ItemStack stack = new ItemStack(item);
            request.initItemStack(stack);
            return new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(item, stack.getCount(), stack.getComponentsPatch()));
        }).toList());
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<? extends CustomIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements CustomIngredientSerializer<SlashBladeIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier RES = SlashBlade.prefix("blade");

        @Override
        public Identifier getIdentifier() {
            return RES;
        }

        @Override
        public MapCodec<SlashBladeIngredient> getCodec() {
            return SlashBladeIngredient.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SlashBladeIngredient> getStreamCodec() {
            return SlashBladeIngredient.STREAM_CODEC;
        }
    }
}
