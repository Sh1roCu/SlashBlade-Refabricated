package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.LivingHurtEvent;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
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
        LivingHurtEvent.CALLBACK.register(this::onLivingDeathEvent);
    }

    /**
     * Not reached if canceled.
     */
    public void onLivingDeathEvent(LivingHurtEvent event) {

        LivingEntity victim = event.getEntity();
        if (victim != null)
            CapabilityConcentrationRank.RANK_POINT.maybeGet(victim)
                    .ifPresent(cr -> cr.addRankPoint(victim, -cr.getUnitCapacity()));

        Entity trueSource = event.getSource().getEntity();
        if (!(trueSource instanceof LivingEntity))
            return;

        LivingEntity sourceEntity = (LivingEntity) trueSource;
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(sourceEntity.getMainHandItem()).isEmpty())
            return;

        CapabilityConcentrationRank.RANK_POINT.maybeGet(trueSource)
                .ifPresent(cr -> cr.addRankPoint(event.getSource()));
    }
}
