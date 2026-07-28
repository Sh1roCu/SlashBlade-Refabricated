package mods.flammpfeil.slashblade.recipe;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class ProudsoulShapelessRecipe extends ShapelessRecipe {
    final ItemStackTemplate result;
    final List<Ingredient> ingredients;

    public ProudsoulShapelessRecipe(Recipe.CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate itemStack, List<Ingredient> nonNullList) {
        super(commonInfo, bookInfo, itemStack, nonNullList);
        this.result = itemStack;
        this.ingredients = nonNullList;
    }

    public static final MapCodec<ProudsoulShapelessRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
                            Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(o -> o.ingredients)
                    )
                    .apply(i, ProudsoulShapelessRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ProudsoulShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            o -> o.ingredients,
            ProudsoulShapelessRecipe::new
    );

    public static final RecipeSerializer<ProudsoulShapelessRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public ItemStack assemble(CraftingInput container) {
        ItemStack result = super.assemble(container);

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
}
