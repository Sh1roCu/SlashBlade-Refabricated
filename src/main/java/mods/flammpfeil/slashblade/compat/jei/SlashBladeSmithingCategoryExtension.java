package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.List;
import java.util.Objects;

public class SlashBladeSmithingCategoryExtension implements ISmithingCategoryExtension<SlashBladeSmithingRecipe> {
    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.template();
        ingredientAcceptor.add(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.base();
        ingredientAcceptor.add(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.addition();
        ingredientAcceptor.add(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient templateIngredient = recipe.template();
        Ingredient baseIngredient = recipe.base();
        Ingredient additionIngredient = recipe.addition();

        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(minecraft.level));

        List<ItemStack> additions = additionIngredient.display().resolveForStacks(contextmap);
        if (additions.isEmpty()) {
            return;
        }
        ItemStack addition = additions.getFirst();

        for (ItemStack template : templateIngredient.display().resolveForStacks(contextmap)) {
            for (ItemStack base : baseIngredient.display().resolveForStacks(contextmap)) {
                SmithingRecipeInput recipeInput = createInput(template, base, addition);
                ItemStack output = assembleResultItem(recipeInput, recipe);
                ingredientAcceptor.add(output);
            }
        }
    }

    private static ItemStack assembleResultItem(SmithingRecipeInput input, SlashBladeSmithingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        return recipe.assemble(input);
    }

    private static SmithingRecipeInput createInput(ItemStack template, ItemStack base, ItemStack addition) {
        return new SmithingRecipeInput(template, base, addition);
    }
}