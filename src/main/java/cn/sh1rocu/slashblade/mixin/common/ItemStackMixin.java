package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponentType;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
