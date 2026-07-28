package mods.flammpfeil.slashblade.patchouli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import vazkii.patchouli.client.book.page.PageSmithing;

@Mixin(PageSmithing.class)
public class PageSmithingMixin {
    // TODO?
//    @Inject(method = "getBase", at = @At("RETURN"), cancellable = true, remap = false)
//    private void getBaseMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
//        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
//            cir.setReturnValue(slashbladeRecipe.base());
//    }
//
//    @Inject(method = "getAddition", at = @At("RETURN"), cancellable = true, remap = false)
//    private void getAdditionMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
//        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
//            cir.setReturnValue(slashbladeRecipe.addition());
//    }
//
//    @Inject(method = "getTemplate", at = @At("RETURN"), cancellable = true, remap = false)
//    private void getTemplateMixin(SmithingRecipe recipe, CallbackInfoReturnable<Ingredient> cir) {
//        if (recipe instanceof SlashBladeSmithingRecipe slashbladeRecipe)
//            cir.setReturnValue(slashbladeRecipe.template());
//    }
}