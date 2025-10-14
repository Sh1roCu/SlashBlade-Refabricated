package mods.flammpfeil.slashblade.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiShapedRecipe;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.List;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    public static final EmiRecipeCategory SLASHBLADE_SMITHING_CATEGORY = new EmiRecipeCategory(
            SlashBlade.prefix("slashblade_smithing"),
            EmiStack.of(Blocks.SMITHING_TABLE)
    );
    public static final EmiRecipeCategory SLASHBLADE_SHAPED_CATEGORY = new EmiRecipeCategory(
            SlashBlade.prefix("shaped_blade"),
            EmiStack.of(Blocks.CRAFTING_TABLE)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(SLASHBLADE_SMITHING_CATEGORY);
        registry.addCategory(SLASHBLADE_SHAPED_CATEGORY);

        // 不知道为啥1.21不会自动注册拔刀剑的自定义ShapedRecipe
        findRecipesByType(RecipeType.CRAFTING).stream()
                .filter(r -> r.value() instanceof SlashBladeShapedRecipe)
                .map(r -> (SlashBladeShapedRecipe) r.value())
                .forEach(recipe -> registry.addRecipe(new EmiShapedRecipe(recipe)));

        // 注册SlashBlade锻造配方
        HashSet<ResourceLocation> vanillaSmithing = new HashSet<>();
        List<RecipeHolder<SlashBladeSmithingRecipe>> smithingRecipes = findRecipesByType(RecipeType.SMITHING).stream()
                .filter(r -> r.value() instanceof SlashBladeSmithingRecipe)
                .map(r -> new RecipeHolder<>(r.id(), (SlashBladeSmithingRecipe) r.value())).toList();
        for (RecipeHolder<SlashBladeSmithingRecipe> recipe : smithingRecipes) {
            registry.addRecipe(new SlashBladeSmithingEmiRecipe(recipe.value()));
            vanillaSmithing.add(recipe.id());
        }
        registry.removeRecipes(emiRecipe -> vanillaSmithing.contains(emiRecipe.getId()) && !(emiRecipe instanceof SlashBladeSmithingEmiRecipe));

        // 添加工作站
        registry.addWorkstation(SLASHBLADE_SMITHING_CATEGORY, EmiStack.of(Blocks.SMITHING_TABLE));
        registry.removeRecipes(ResourceLocation.tryParse("emi:/crafting/repairing/slashblade/slashblade"));

    }

    private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> findRecipesByType(RecipeType<T> type) {
        Minecraft instance = Minecraft.getInstance();
        return instance.level.getRecipeManager().getAllRecipesFor(type);
    }
}