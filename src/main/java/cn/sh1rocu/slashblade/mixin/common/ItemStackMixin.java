package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    public void psb$itemSetDamage(int damage, CallbackInfo ci) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            blade.setDamage((ItemStack) (Object) this, damage);
            ci.cancel();
        }
    }

    @ModifyArg(method = "hurtAndBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z"))
    public <T extends LivingEntity> int am$modifyBreakAmount(int amount, @Local(argsOnly = true) T entity, @Local(argsOnly = true) Consumer<T> onBroken) {
        if (this.getItem() instanceof ItemSlashBladeExtension blade) {
            return blade.damageItem((ItemStack) (Object) this, amount, entity, onBroken);
        }
        return amount;
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    public void sb$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getMaxDamage((ItemStack) (Object) this));
        }
    }

    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    public void sb$itemDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getDamage((ItemStack) (Object) this));
        }
    }
}
