package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

public class SlashBladeSmithingCategoryExtension implements ISmithingCategoryExtension<SlashBladeSmithingRecipe> {
    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.template();
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.base();
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient ingredient = recipe.addition();
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(SlashBladeSmithingRecipe recipe, T ingredientAcceptor) {
        Ingredient templateIngredient = recipe.template();
        Ingredient baseIngredient = recipe.base();
        Ingredient additionIngredient = recipe.addition();

        ItemStack[] additions = additionIngredient.getItems();
        if (additions.length == 0) {
            return;
        }
        ItemStack addition = additions[0];

        for (ItemStack template : templateIngredient.getItems()) {
            for (ItemStack base : baseIngredient.getItems()) {
                SmithingRecipeInput recipeInput = createInput(template, base, addition);
                ItemStack output = assembleResultItem(recipeInput, recipe);
                ingredientAcceptor.addItemStack(output);
            }
        }
    }

    private static ItemStack assembleResultItem(SmithingRecipeInput input, SlashBladeSmithingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.assemble(input, registryAccess);
    }

    private static SmithingRecipeInput createInput(ItemStack template, ItemStack base, ItemStack addition) {
        return new SmithingRecipeInput(template, base, addition);
    }
}