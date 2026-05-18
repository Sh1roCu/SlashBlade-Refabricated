package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
            blade.sb$setDamage((ItemStack) (Object) this, damage);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int sb$modifyBreakAmount(int amount, @Local(argsOnly = true) ServerPlayer entity, @Local(argsOnly = true) Consumer<Item> onBroken) {
        if (this.getItem() instanceof ItemSlashBladeExtension blade) {
            return blade.sb$damageItem((ItemStack) (Object) this, amount, entity, onBroken);
        }
        return amount;
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void sb$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.sb$getMaxDamage((ItemStack) (Object) this));
        }
    }

    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    private void sb$itemDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.sb$getDamage((ItemStack) (Object) this));
        }
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void sb$itemRarity(CallbackInfoReturnable<Rarity> cir) {
        if (getItem() instanceof ItemSlashBladeExtension blade) {
            cir.setReturnValue(blade.getRarity((ItemStack) (Object) this));
        }
    }

    @ModifyExpressionValue(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object sb$getDefaultAttributeModifiers(Object original) {
        return sb$modifyAttributes((ItemStack) (Object) this, (ItemAttributeModifiers) original);
    }

    @ModifyExpressionValue(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object sb$getDefaultAttributeModifiers_(Object original) {
        return sb$modifyAttributes((ItemStack) (Object) this, (ItemAttributeModifiers) original);
    }

    @Unique
    private static ItemAttributeModifiers sb$modifyAttributes(ItemStack stack, ItemAttributeModifiers original) {
        if (stack.getItem() instanceof ItemSlashBladeExtension blade) {
            return blade.getDefaultAttributeModifiers(stack);
        }
        return original;
    }
}
