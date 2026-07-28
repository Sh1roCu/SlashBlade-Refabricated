package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class RankPointHandler {
    private static final class SingletonHolder {
        private static final RankPointHandler instance = new RankPointHandler();
    }

    public static RankPointHandler getInstance() {
        return SingletonHolder.instance;
    }

    private RankPointHandler() {
    }

    public void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(this::onLivingDeathEvent);
    }

    /**
     * Not reached if canceled.
     */
    public void onLivingDeathEvent(LivingEntity victim, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked) {
        if (victim != null)
            CapabilityConcentrationRank.RANK_POINT.maybeGet(victim)
                    .ifPresent(cr -> cr.addRankPoint(victim, -cr.getUnitCapacity()));

        Entity trueSource = source.getEntity();
        if (!(trueSource instanceof LivingEntity sourceEntity))
            return;

        if (CapabilitySlashBlade.getBladeState(sourceEntity.getMainHandItem()).isEmpty())
            return;

        CapabilityConcentrationRank.RANK_POINT.maybeGet(trueSource)
                .ifPresent(cr -> cr.addRankPoint(source));
    }
}
