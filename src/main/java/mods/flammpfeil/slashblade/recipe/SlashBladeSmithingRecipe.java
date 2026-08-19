package mods.flammpfeil.slashblade.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.handler.RegistryHandler;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record SlashBladeSmithingRecipe(Identifier outputBlade, Ingredient template, Ingredient base,
                                       Ingredient addition) implements SmithingRecipe {
    public static final MapCodec<SlashBladeSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("blade").forGetter(SlashBladeSmithingRecipe::outputBlade),
            Ingredient.CODEC.fieldOf("template").forGetter(SlashBladeSmithingRecipe::template),
            Ingredient.CODEC.fieldOf("base").forGetter(SlashBladeSmithingRecipe::base),
            Ingredient.CODEC.fieldOf("addition").forGetter(SlashBladeSmithingRecipe::addition)
    ).apply(instance, SlashBladeSmithingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlashBladeSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            SlashBladeSmithingRecipe::outputBlade,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::template,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::addition,
            SlashBladeSmithingRecipe::new
    );

    public static final RecipeSerializer<SlashBladeSmithingRecipe> SERIALIZER = new RecipeSerializer<>(SlashBladeSmithingRecipe.CODEC, SlashBladeSmithingRecipe.STREAM_CODEC);


    private static ItemStack getResultBlade(Identifier outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.getValue(outputBlade)
                : SBItems.SLASHBLADE;

        return bladeItem.getDefaultInstance();
    }

    public @NotNull ItemStack getResultItem() {
        ItemStack result = SlashBladeSmithingRecipe.getResultBlade(outputBlade);

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(outputBlade)) {
            result = RegistryHandler.DEFINITIONS.get(outputBlade).getBlade();
        }

        return result;
    }

    @Override
    public boolean matches(SmithingRecipeInput container, @NotNull Level level) {
        return this.template.test(container.getItem(0)) && this.base.test(container.getItem(1)) && this.addition.test(container.getItem(2));
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(template);
    }

    @Override
    public Ingredient baseIngredient() {
        return base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SmithingRecipeInput container) {
        var result = this.getResultItem();
        if (!(result.getItem() instanceof ItemSlashBlade)) {
            result = new ItemStack(SBItems.SLASHBLADE);
        }

        var resultState = CapabilitySlashBlade.getBladeState(result).orElseThrow(NullPointerException::new);
        var stack = container.getItem(1);
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return ItemStack.EMPTY;
        var ingredientState = CapabilitySlashBlade.getBladeState(stack).orElseThrow(NullPointerException::new);

        resultState.setProudSoulCount(resultState.getProudSoulCount() + ingredientState.getProudSoulCount());
        resultState.setKillCount(
                SlashBladeConfig.DO_CRAFTING_SUM_REFINE.get() ?
                        Math.max(resultState.getKillCount(), ingredientState.getKillCount()) :
                        resultState.getKillCount() + ingredientState.getKillCount()
        );
        resultState.setRefine(resultState.getRefine() + ingredientState.getRefine());
        updateEnchantment(result, stack);

        return result;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return SlashBladeSmithingRecipe.SERIALIZER;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(this.template, this.base, this.addition));
    }

    private void updateEnchantment(ItemStack result, ItemStack ingredient) {
        var newItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(result);
        var oldItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(ingredient);
        for (Holder<Enchantment> enchantIndex : oldItemEnchants.keySet()) {
            Enchantment enchantment = enchantIndex.value();

            int destLevel = Math.max(newItemEnchants.getLevel(enchantIndex), 0);
            int srcLevel = oldItemEnchants.getLevel(enchantIndex);

            srcLevel = Math.max(srcLevel, destLevel);
            srcLevel = Math.min(srcLevel, enchantment.getMaxLevel());

            boolean canApplyFlag = result.canBeEnchantedWith(enchantIndex, EnchantingContext.ACCEPTABLE);
            if (canApplyFlag) {
                for (Holder<Enchantment> curEnchantIndex : newItemEnchants.keySet()) {
                    if (curEnchantIndex.value() != enchantment
                            && !Enchantment.areCompatible(enchantIndex, curEnchantIndex)) {
                        canApplyFlag = false;
                        break;
                    }
                }
                if (canApplyFlag) {
                    int finalSrcLevel = srcLevel;
                    EnchantmentHelper.updateEnchantments(result, mutable -> mutable.set(enchantIndex, finalSrcLevel));
                }
            }
        }
    }
}