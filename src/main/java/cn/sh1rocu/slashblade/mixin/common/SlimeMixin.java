package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.util.CommonHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public class SlimeMixin {
    @Inject(method = "jumpFromGround", at = @At(
            value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Slime;needsSync:Z",
            shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void sb$jumpFromGround(CallbackInfo ci) {
        CommonHooks.onLivingJump((LivingEntity) (Object) this);
    }
}
