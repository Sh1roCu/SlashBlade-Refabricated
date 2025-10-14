package mods.flammpfeil.slashblade.event.handler;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
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
        LivingHurtEvent.EVENT.register(this::onLivingDeathEvent);
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
        if (CapabilitySlashBlade.getBladeState(sourceEntity.getMainHandItem()).isEmpty())
            return;

        CapabilityConcentrationRank.RANK_POINT.maybeGet(trueSource)
                .ifPresent(cr -> cr.addRankPoint(event.getSource()));
    }
}
