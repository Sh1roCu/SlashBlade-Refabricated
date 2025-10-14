package mods.flammpfeil.slashblade.patchouli.mixin;

import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.page.PageSmithing;

@Mixin(PageSmithing.class)
public class PageSmithingMixin {
    @Inject(method = "getBase", at = @At("RETURN"), cancellable = true, remap = false)
    private void getBaseMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
            cir.setReturnValue(slashbladeRecipe.base());
    }

    @Inject(method = "getAddition", at = @At("RETURN"), cancellable = true, remap = false)
    private void getAdditionMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
            cir.setReturnValue(slashbladeRecipe.addition());
    }

    @Inject(method = "getTemplate", at = @At("RETURN"), cancellable = true, remap = false)
    private void getTemplateMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
            cir.setReturnValue(slashbladeRecipe.template());
    }
}