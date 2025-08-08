package mods.flammpfeil.slashblade.ability;

import cn.sh1rocu.slashblade.api.event.EntityJoinLevelEvent;
import cn.sh1rocu.slashblade.api.event.LivingTickEvent;
import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.capability.mobeffect.IMobEffectState;
import mods.flammpfeil.slashblade.entity.ai.StunGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

/**
 * Created by Furia on 15/06/20.
 */
public class StunManager {

    static final int DEFAULT_STUN_TICKS = 10;

    public static void init() {
        EntityJoinLevelEvent.CALLBACK.register(StunManager::onEntityJoinWorldEvent);
        LivingTickEvent.CALLBACK.register(StunManager::onEntityLivingUpdate);
    }

    public static void onEntityJoinWorldEvent(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof PathfinderMob))
            return;
        PathfinderMob entity = (PathfinderMob) event.getEntity();

        entity.goalSelector.addGoal(-1, new StunGoal(entity));
    }

    public static void onEntityLivingUpdate(LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof PathfinderMob))
            return;
        if (target.level() == null)
            return;

        boolean onStun = CapabilityMobEffect.MOB_EFFECT.maybeGet(target)
                .filter((state) -> state.isStun(target.level().getGameTime())).isPresent();

        if (onStun) {
            Vec3 motion = target.getDeltaMovement();
            if (5 < target.fallDistance)
                target.setDeltaMovement(motion.x, motion.y - 2.0f, motion.z);
            else if (motion.y < 0)
                target.setDeltaMovement(motion.x, motion.y * 0.25f, motion.z);
        }

    }

    public static void setStun(LivingEntity target, LivingEntity attacker) {
        setStun(target);
    }

    public static void setStun(LivingEntity target) {
        setStun(target, DEFAULT_STUN_TICKS);
    }

    public static void setStun(LivingEntity target, long duration) {
        if (!(target instanceof PathfinderMob))
            return;
        if (target.level() == null)
            return;

        CapabilityMobEffect.MOB_EFFECT.maybeGet(target).ifPresent(state -> state.setManagedStun(target.level().getGameTime(), duration));
    }

    public static void removeStun(LivingEntity target) {
        if (target.level() == null)
            return;
        if (!(target instanceof LivingEntity))
            return;

        CapabilityMobEffect.MOB_EFFECT.maybeGet(target).ifPresent(IMobEffectState::clearStunTimeOut);
    }
}
