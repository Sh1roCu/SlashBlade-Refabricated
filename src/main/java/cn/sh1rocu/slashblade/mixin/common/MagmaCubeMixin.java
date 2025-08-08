package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.LivingJumpEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaCube.class)
public class MagmaCubeMixin {
    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    public void sb$onJump(CallbackInfo ci) {
        LivingJumpEvent.CALLBACK.invoker().onLivingEntityJump(new LivingJumpEvent((LivingEntity) (Object) this));
    }
}