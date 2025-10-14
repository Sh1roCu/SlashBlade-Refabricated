package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.BaseItemExtension;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void sb$onHeadTick(CallbackInfo ci) {
        ItemStack stack = getItem();
        ItemEntity self = (ItemEntity) (Object) this;
        if (stack.getItem() instanceof BaseItemExtension extension && extension.onEntityItemUpdate(stack, self)) {
            ci.cancel();
        }
    }


    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void sb$tick(CallbackInfo ci) {
        ItemStack stack = this.getItem();
        if (stack.getItem() instanceof ItemSlashBladeExtension blade) {
            if (blade.onEntityItemUpdate(stack, (ItemEntity) (Object) this))
                ci.cancel();
        }
    }
}