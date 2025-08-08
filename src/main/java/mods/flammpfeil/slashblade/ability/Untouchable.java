package mods.flammpfeil.slashblade.ability;

import cn.sh1rocu.slashblade.api.event.LivingAttackEvent;
import cn.sh1rocu.slashblade.api.event.LivingHurtEvent;
import cn.sh1rocu.slashblade.api.event.LivingJumpEvent;
import cn.sh1rocu.slashblade.api.event.LivingTickEvent;
import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class Untouchable {
    private static final class SingletonHolder {
        private static final Untouchable instance = new Untouchable();
    }

    public static Untouchable getInstance() {
        return SingletonHolder.instance;
    }

    private Untouchable() {
    }

    public void register() {
        LivingHurtEvent.CALLBACK.register(this::onLivingHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::onLivingDamage);
        LivingAttackEvent.CALLBACK.register(this::onLivingAttack);
        ServerLivingEntityEvents.ALLOW_DEATH.register(this::onLivingDeath);
        LivingTickEvent.CALLBACK.register(this::onLivingTicks);
        LivingJumpEvent.CALLBACK.register(this::onPlayerJump);
    }

    public static void setUntouchable(LivingEntity entity, int ticks) {
        CapabilityMobEffect.MOB_EFFECT.maybeGet(entity).ifPresent(ef -> {
            ef.setManagedUntouchable(entity.level().getGameTime(), ticks);
            ef.storeEffects(entity.getActiveEffectsMap().keySet());
            ef.storeHealth(entity.getHealth());
        });
    }

    private boolean checkUntouchable(LivingEntity entity) {
        Optional<Boolean> isUntouchable = CapabilityMobEffect.MOB_EFFECT.maybeGet(entity)
                .map(ef -> ef.isUntouchable(entity.getCommandSenderWorld().getGameTime()));

        return isUntouchable.orElseGet(() -> false);
    }

    private void doWitchTime(Entity entity) {
        if (entity == null)
            return;

        if (!(entity instanceof LivingEntity))
            return;

        StunManager.setStun((LivingEntity) entity);
    }

    public void onLivingHurt(LivingHurtEvent event) {
        if (checkUntouchable(event.getEntity())) {
            event.setCanceled(true);
            doWitchTime(event.getSource().getEntity());
        }
    }

    public boolean onLivingDamage(LivingEntity entity, DamageSource source, float amount) {
        if (checkUntouchable(entity)) {
            //event.setCanceled(true);
            doWitchTime(source.getEntity());
            return false;
        }
        return true;
    }

    public void onLivingAttack(LivingAttackEvent event) {
        if (checkUntouchable(event.getEntity())) {
            event.setCanceled(true);
            doWitchTime(event.getSource().getEntity());
        }
    }

    public boolean onLivingDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (checkUntouchable(entity)) {
            //event.setCanceled(true);
            doWitchTime(damageSource.getEntity());

            CapabilityMobEffect.MOB_EFFECT.maybeGet(entity).ifPresent(ef -> {
                if (ef.hasUntouchableWorked()) {
                    List<MobEffect> filterd = entity.getActiveEffectsMap().keySet().stream()
                            .filter(p -> !(ef.getEffectSet().contains(p) || p.isBeneficial())).toList();

                    filterd.forEach(entity::removeEffect);

                    float storedHealth = ef.getStoredHealth();
                    if (ef.getStoredHealth() < storedHealth)
                        entity.setHealth(ef.getStoredHealth());
                }
            });
            return false;
        }
        return true;
    }

    public void onLivingTicks(LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide())
            return;

        CapabilityMobEffect.MOB_EFFECT.maybeGet(entity).ifPresent(ef -> {
            if (ef.hasUntouchableWorked()) {
                ef.setUntouchableWorked(false);
                List<MobEffect> filterd = entity.getActiveEffectsMap().keySet().stream()
                        .filter(p -> !(ef.getEffectSet().contains(p) || p.isBeneficial())).toList();

                filterd.forEach(entity::removeEffect);

                float storedHealth = ef.getStoredHealth();
                if (ef.getStoredHealth() < storedHealth)
                    entity.setHealth(ef.getStoredHealth());
            }
        });
    }

    static final int JUMP_TICKS = 10;

    public void onPlayerJump(LivingJumpEvent event) {
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(event.getEntity().getMainHandItem()).isEmpty())
            return;

        Untouchable.setUntouchable(event.getEntity(), JUMP_TICKS);
    }
}
