package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    private void sb$itemSetDamage(int damage, CallbackInfo ci) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            blade.setDamage((ItemStack) (Object) this, damage);
            ci.cancel();
        }
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void sb$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getMaxDamage((ItemStack) (Object) this));
        }
    }

    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    private void sb$itemDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getDamage((ItemStack) (Object) this));
        }
    }
}
