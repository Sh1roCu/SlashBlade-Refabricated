package mods.flammpfeil.slashblade.recipe;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ProudsoulShapelessRecipe extends ShapelessRecipe {
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;

    public ProudsoulShapelessRecipe(String string, CraftingBookCategory craftingBookCategory, ItemStack itemStack, NonNullList<Ingredient> nonNullList) {
        super(string, craftingBookCategory, itemStack, nonNullList);
        this.group = string;
        this.category = craftingBookCategory;
        this.result = itemStack;
        this.ingredients = nonNullList;
    }

    public static final RecipeSerializer<ProudsoulShapelessRecipe> SERIALIZER = new Serializer();

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider access) {
        ItemStack result = super.assemble(container, access);

        for (ItemStack stack : container.items()) {
            if (stack.isEmpty())
                continue;
            if (!stack.isEnchanted())
                continue;

            ItemEnchantments emap = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            EnchantmentHelper.updateEnchantments(result, mutable -> {
                emap.entrySet().forEach(entry -> {
                    int destLevel = mutable.getLevel(entry.getKey());
                    int srcLevel = entry.getIntValue();

                    srcLevel = Math.max(srcLevel, destLevel);
                    srcLevel = Math.min(srcLevel, entry.getKey().value().getMaxLevel());

                    mutable.upgrade(entry.getKey(), srcLevel);
                });
            });
        }

        return result;
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        boolean result = super.matches(container, level);

        if (result) {
            Map<Holder<Enchantment>, Integer> all = Maps.newHashMap();

            int soulCount = 0;

            for (ItemStack stack : container.items()) {
                if (stack.isEmpty())
                    continue;
                if (!stack.isEnchanted())
                    continue;

                soulCount++;

                ItemEnchantments emap = EnchantmentHelper.getEnchantmentsForCrafting(stack);

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : emap.entrySet()) {
                    if (all.containsKey(entry.getKey())) {

                        int value = all.get(entry.getKey()) + entry.getIntValue();

                        all.put(entry.getKey(), value);
                    } else {
                        all.put(entry.getKey(), entry.getIntValue());
                    }
                }
            }

            result = all.size() == 1 || all.isEmpty();
            if (result) {
                for (Map.Entry<Holder<Enchantment>, Integer> entry : all.entrySet()) {
                    result = entry.getValue() == soulCount;
                }
            }
        }

        return result;
    }

    public static class Serializer implements RecipeSerializer<ProudsoulShapelessRecipe> {
        private static final MapCodec<ProudsoulShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter((shapelessRecipe) -> shapelessRecipe.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((shapelessRecipe) -> shapelessRecipe.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter((shapelessRecipe) -> shapelessRecipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
            Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            if (ingredients.length == 0) {
                return DataResult.error(() -> "No ingredients for shapeless recipe");
            } else {
                return ingredients.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe") : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
            }
        }, DataResult::success).forGetter((shapelessRecipe) -> shapelessRecipe.ingredients)).apply(instance, ProudsoulShapelessRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ProudsoulShapelessRecipe> STREAM_CODEC = StreamCodec.of(ProudsoulShapelessRecipe.Serializer::toNetwork, ProudsoulShapelessRecipe.Serializer::fromNetwork);

        private static ProudsoulShapelessRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            String string = registryFriendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = registryFriendlyByteBuf.readEnum(CraftingBookCategory.class);
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            return new ProudsoulShapelessRecipe(string, craftingBookCategory, itemStack, nonNullList);
        }

        private static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, ProudsoulShapelessRecipe recipe) {
            registryFriendlyByteBuf.writeUtf(recipe.group);
            registryFriendlyByteBuf.writeEnum(recipe.category);
            registryFriendlyByteBuf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.result);
        }

        @Override
        public MapCodec<ProudsoulShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ProudsoulShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
