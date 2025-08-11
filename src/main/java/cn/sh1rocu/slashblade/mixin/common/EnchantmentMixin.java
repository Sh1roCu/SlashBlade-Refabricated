package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.IEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void sb$canApplyAtEnchantingTable(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment self = (Enchantment) (Object) this;
        if (itemStack.getItem() instanceof IEnchantment item) {
            cir.setReturnValue(item.canApplyAtEnchantingTable(itemStack, self));
        }
    }
}
