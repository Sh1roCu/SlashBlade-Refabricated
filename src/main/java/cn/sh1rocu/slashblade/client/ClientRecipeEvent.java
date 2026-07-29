package cn.sh1rocu.slashblade.client;


import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.recipe.RecipeSerializerRegistry;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.List;

public class ClientRecipeEvent {
    public static List<RecipeHolder<CraftingRecipe>> SB_RECIPES = Collections.emptyList();

    public static void onRecipeReceived(Minecraft client, SynchronizedRecipes recipes) {
        SB_RECIPES = Lists.newArrayList(recipes.getAllOfType(RecipeSerializerRegistry.SLASHBLADE_SHAPED_TYPE));
    }
}