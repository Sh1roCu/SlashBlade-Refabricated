package mods.flammpfeil.slashblade.entity.ai;

import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.capability.mobeffect.IMobEffectState;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StunGoal extends Goal {
    private final PathfinderMob entity;

    public StunGoal(PathfinderMob creature) {
        this.entity = creature;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK, Flag.TARGET));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean canUse() {
        boolean onStun = CapabilityMobEffect.MOB_EFFECT.maybeGet(this.entity)
                .filter((state) -> state.isStun(this.entity.level().getGameTime())).isPresent();

        return onStun;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by
     * another one
     */
    public void stop() {
        CapabilityMobEffect.MOB_EFFECT.maybeGet(this.entity).ifPresent(IMobEffectState::clearStunTimeOut);
    }
}
