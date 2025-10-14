package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<? super T> dataComponentType, @Nullable T object);

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    private void sb$itemSetDamage(int damage, CallbackInfo ci) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            blade.setDamage((ItemStack) (Object) this, damage);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int sb$modifyBreakAmount(int amount, @Local(argsOnly = true) ServerPlayer entity, @Local(argsOnly = true) Consumer<Item> onBroken) {
        if (this.getItem() instanceof ItemSlashBladeExtension blade) {
            return blade.damageItem((ItemStack) (Object) this, amount, entity, onBroken);
        }
        return amount;
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

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void sb$itemRarity(CallbackInfoReturnable<Rarity> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getRarity((ItemStack) (Object) this));
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
    private void sb$initItemStack(ItemLike itemLike, int i, PatchedDataComponentMap patchedDataComponentMap, CallbackInfo ci) {
        if (itemLike.asItem() instanceof ItemSlashBladeExtension blade) {
            this.set(DataComponents.ATTRIBUTE_MODIFIERS, blade.getDefaultAttributeModifiers((ItemStack) (Object) this));
        }
    }
}
