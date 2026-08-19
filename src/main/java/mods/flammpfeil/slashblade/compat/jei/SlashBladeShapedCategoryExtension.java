package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.library.plugins.vanilla.crafting.CraftingCategoryExtension;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SlashBladeShapedCategoryExtension extends CraftingCategoryExtension {
    @Override
    public int getWidth(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = recipeHolder.value();
        if (recipe instanceof SlashBladeShapedRecipe shapedRecipe) {
            return shapedRecipe.getWidth();
        }
        return super.getWidth(recipeHolder);
    }

    @Override
    public int getHeight(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = recipeHolder.value();
        if (recipe instanceof SlashBladeShapedRecipe shapedRecipe) {
            return shapedRecipe.getHeight();
        }
        return super.getHeight(recipeHolder);
    }
}
