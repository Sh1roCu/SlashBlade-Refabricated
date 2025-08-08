package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.event.LivingAttackEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "hurt", at = @At("HEAD"))
    public void sb$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingAttackEvent event = new LivingAttackEvent((LivingEntity) (Object) this, source, amount);
        LivingAttackEvent.CALLBACK.invoker().onLivingAttack(event);
    }
}