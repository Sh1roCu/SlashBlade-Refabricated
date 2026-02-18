package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.LivingTickEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtension {
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"), cancellable = true)
    private void sb$tickEvent(CallbackInfo ci) {
        LivingTickEvent event = new LivingTickEvent((LivingEntity) (Object) this);
        LivingTickEvent.CALLBACK.invoker().onLivingTick(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void sb$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
        ItemStack stack = getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemSlashBladeExtension blade) {
            if (blade.sb$onEntitySwing(stack, (LivingEntity) (Object) this))
                ci.cancel();
        }
    }
}