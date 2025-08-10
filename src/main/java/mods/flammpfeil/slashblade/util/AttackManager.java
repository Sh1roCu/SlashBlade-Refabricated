package mods.flammpfeil.slashblade.util;

import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.ability.ArrowReflector;
import mods.flammpfeil.slashblade.ability.TNTExtinguisher;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static mods.flammpfeil.slashblade.SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER;

public class AttackManager {
    public static boolean isPowered(LivingEntity entity) {
        ItemStack blade = entity.getMainHandItem();
        boolean result = entity.hasEffect(MobEffects.DAMAGE_BOOST) || entity.hasEffect(MobEffects.HUNGER);
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(blade).isPresent()) {
            var state = CapabilitySlashBlade.BLADESTATE.maybeGet(blade).orElseThrow(NullPointerException::new);
            var event = new SlashBladeEvent.PowerBladeEvent(blade, state, entity, result);
            SlashBladeEvent.POWER_BLADE.invoker().onPower(event);
            result = event.isPowered();
        }

        return result;
    }

    public static void areaAttack(LivingEntity playerIn, Consumer<LivingEntity> beforeHit) {
        areaAttack(playerIn, beforeHit, 1.0f, true, true, false);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll) {
        return doSlash(playerIn, roll, false);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, boolean mute) {
        return doSlash(playerIn, roll, mute, false);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, boolean mute, boolean critical) {
        return doSlash(playerIn, roll, mute, critical, 1.0);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, boolean mute, boolean critical,
                                            double comboRatio) {
        return doSlash(playerIn, roll, Vec3.ZERO, mute, critical, comboRatio);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, Vec3 centerOffset, boolean mute,
                                            boolean critical, double comboRatio) {
        return doSlash(playerIn, roll, centerOffset, mute, critical, comboRatio, KnockBacks.cancel);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, Vec3 centerOffset, boolean mute,
                                            boolean critical, double comboRatio, KnockBacks knockback) {

        int colorCode = CapabilitySlashBlade.BLADESTATE.maybeGet(playerIn.getMainHandItem())
                .map(ISlashBladeState::getColorCode).orElse(0xFFFFFF);

        return doSlash(playerIn, roll, colorCode, centerOffset, mute, critical, comboRatio, knockback);
    }

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, int colorCode, Vec3 centerOffset,
                                            boolean mute, boolean critical, double comboRatio, KnockBacks knockback) {

        if (playerIn.level().isClientSide()) {
            return null;
        }
        ItemStack blade = playerIn.getMainHandItem();
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(blade).isEmpty())
            return null;
        SlashBladeEvent.DoSlashEvent event = new SlashBladeEvent.DoSlashEvent(blade, CapabilitySlashBlade.BLADESTATE.maybeGet(blade).orElseThrow(NullPointerException::new),
                playerIn, roll, critical, comboRatio, knockback);
        SlashBladeEvent.DO_SLASH.invoker().onDoSlash(event);
        if (event.isCanceled())
            return null;
        Vec3 pos = playerIn.position().add(0.0D, (double) playerIn.getEyeHeight() * 0.75D, 0.0D)
                .add(playerIn.getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, playerIn.getViewYRot(0)).scale(centerOffset.y))
                .add(VectorHelper.getVectorForRotation(0, playerIn.getViewYRot(0) + 90).scale(centerOffset.z))
                .add(playerIn.getLookAngle().scale(centerOffset.z));

        EntitySlashEffect jc = new EntitySlashEffect(SBEntityTypes.SlashEffect, playerIn.level());
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setOwner(playerIn);
        jc.setRotationRoll(roll);
        jc.setYRot(playerIn.getYRot());
        jc.setXRot(0);

        jc.setColor(colorCode);

        jc.setMute(mute);
        jc.setIsCritical(critical);

        jc.setDamage(comboRatio);

        jc.setKnockBack(knockback);

        CapabilityConcentrationRank.RANK_POINT.maybeGet(playerIn)
                .ifPresent(rank -> jc.setRank(rank.getRankLevel(playerIn.level().getGameTime())));

        playerIn.level().addFreshEntity(jc);

        return jc;
    }

    public static void doVoidSlashAttack(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        Vec3 pos = living.position().add(0.0D, (double) living.getEyeHeight() * 0.75D, 0.0D)
                .add(living.getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, living.getViewYRot(0)).scale(Vec3.ZERO.y))
                .add(VectorHelper.getVectorForRotation(0, living.getViewYRot(0) + 90).scale(Vec3.ZERO.z))
                .add(living.getLookAngle().scale(Vec3.ZERO.z));

        EntitySlashEffect jc = newVoidSlashEffect(living, pos);

        jc.setDamage(0D);

        jc.setKnockBack(KnockBacks.cancel);

        CapabilityConcentrationRank.RANK_POINT.maybeGet(living)
                .ifPresent(rank -> jc.setRank(rank.getRankLevel(living.level().getGameTime())));

        jc.setLifetime(36);

        living.level().addFreshEntity(jc);
    }

    public static @NotNull EntitySlashEffect newVoidSlashEffect(LivingEntity living, Vec3 pos) {
        EntitySlashEffect jc = new EntitySlashEffect(SBEntityTypes.SlashEffect, living.level()) {

            @Override
            public double getDamage() {
                return 0;
            }

            @Override
            public SoundEvent getSlashSound() {
                return SoundEvents.BLAZE_HURT;
            }

            @Override
            protected void tryDespawn() {
                if (!this.level().isClientSide()) {
                    if (this.getLifetime() < this.tickCount) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F,
                                0.625F + 0.1f * this.random.nextFloat());
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.ENCHANTED_HIT, this.getX(),
                                this.getY(), this.getZ(), 16, 0.7, 0.7, 0.7, 0.02);
                        this.getAlreadyHits().forEach(entity -> {

                            if (entity.isAlive()) {
                                float yRot = this.getOwner() != null ? this.getOwner().getYRot() : 0;
                                entity.addDeltaMovement(new Vec3(
                                        -Math.sin(yRot * (float) Math.PI / 180.0F) * 0.5,
                                        0.05D,
                                        Math.cos(yRot * (float) Math.PI / 180.0F) * 0.5));
                                double baseAmount = living.getAttributeValue(Attributes.ATTACK_DAMAGE);
                                int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, living.getMainHandItem());
                                baseAmount *= 1 + powerLevel * 0.1;
                                baseAmount += AttackHelper.getRankBonus(living);
                                if (this.getShooter() instanceof LivingEntity shooter) {
                                    baseAmount *= getSlashBladeDamageScale(shooter) * SLASHBLADE_DAMAGE_MULTIPLIER.get();
                                }
                                doAttackWith(this.damageSources().indirectMagic(this, this.getShooter()),
                                        ((float) (baseAmount) * 5.1f), entity, true, true);
                            }
                        });
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
            }
        };
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setOwner(living);

        jc.setRotationRoll(180);
        jc.setYRot(living.getYRot() - 22.5F);
        jc.setXRot(0);

        int colorCode = CapabilitySlashBlade.BLADESTATE.maybeGet(living.getMainHandItem())
                .map(ISlashBladeState::getColorCode).orElse(0xFFFFFF);
        jc.setColor(colorCode);

        jc.setMute(false);
        jc.setIsCritical(false);

        return jc;
    }

    public static List<Entity> areaAttack(LivingEntity playerIn, Consumer<LivingEntity> beforeHit, float comboRatio,
                                          boolean forceHit, boolean resetHit, boolean mute) {
        return areaAttack(playerIn, beforeHit, comboRatio, forceHit, resetHit, mute, null);
    }

    public static List<Entity> areaAttack(LivingEntity playerIn, Consumer<LivingEntity> beforeHit, float comboRatio,
                                          boolean forceHit, boolean resetHit, boolean mute, List<Entity> exclude) {
        List<Entity> founds = Lists.newArrayList();

        if (!playerIn.level().isClientSide()) {
            founds = TargetSelector.getTargettableEntitiesWithinAABB(playerIn.level(), playerIn);

            if (exclude != null) {
                founds.removeAll(exclude);
            }

            for (Entity entity : founds) {
                if (entity instanceof LivingEntity living) {
                    beforeHit.accept(living);
                }
                doMeleeAttack(playerIn, entity, forceHit, resetHit, comboRatio);
            }
        }

        if (!mute) {
            playerIn.level().playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5F,
                    0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
        }
        return founds;
    }

    public static <E extends Entity & IShootable> List<Entity> areaAttack(E owner, Consumer<LivingEntity> beforeHit,
                                                                          double reach, boolean forceHit, boolean resetHit) {
        return areaAttack(owner, beforeHit, reach, forceHit, resetHit, null);
    }

    public static <E extends Entity & IShootable> List<Entity> areaAttack(E owner, Consumer<LivingEntity> beforeHit,
                                                                          double reach, boolean forceHit, boolean resetHit, List<Entity> exclude) {

        return areaAttack(owner, beforeHit, reach, forceHit, resetHit, 1.0F, exclude);
    }

    public static <E extends Entity & IShootable> List<Entity> areaAttack(E owner, Consumer<LivingEntity> beforeHit,
                                                                          double reach, boolean forceHit, boolean resetHit, float comboRatio, List<Entity> exclude) {
        List<Entity> founds = Lists.newArrayList();

        if (!owner.level().isClientSide()) {
            founds = TargetSelector.getTargettableEntitiesWithinAABB(owner.level(), reach, owner);

            if (exclude != null) {
                founds.removeAll(exclude);
            }

            for (Entity entity : founds) {

                if (entity instanceof LivingEntity living) {
                    beforeHit.accept(living);
                }

                double baseAmount = owner.getDamage();
                if (owner.getShooter() instanceof LivingEntity living) {
                    if (!(owner instanceof EntitySlashEffect)) {
                        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, living.getMainHandItem());
                        baseAmount += powerLevel * 0.1;
                    }
                    baseAmount *= living.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    baseAmount += AttackHelper.getRankBonus(living);

                    baseAmount *= comboRatio * getSlashBladeDamageScale(living) * SLASHBLADE_DAMAGE_MULTIPLIER.get();

                }

                doAttackWith(owner.damageSources().indirectMagic(owner, owner.getShooter()), (float) baseAmount, entity,
                        forceHit, resetHit);
            }
        }

        return founds;
    }

    public static void doManagedAttack(Consumer<Entity> attack, Entity target, boolean forceHit, boolean resetHit) {
        if (forceHit) {
            target.invulnerableTime = 0;
        }

        attack.accept(target);

        if (resetHit) {
            target.invulnerableTime = 0;
        }
    }

    public static void doAttackWith(DamageSource src, float amount, Entity target, boolean forceHit, boolean resetHit) {
        if (target instanceof EntityAbstractSummonedSword) {
            return;
        }

        doManagedAttack((t) -> t.hurt(src, amount), target, forceHit, resetHit);
    }

    public static void doMeleeAttack(LivingEntity attacker, Entity target, boolean forceHit, boolean resetHit) {
        doMeleeAttack(attacker, target, forceHit, resetHit, 1.0f);
    }

    public static void doMeleeAttack(LivingEntity attacker, Entity target, boolean forceHit, boolean resetHit, float comboRatio) {
        doManagedAttack((t) -> CapabilitySlashBlade.BLADESTATE.maybeGet(attacker.getMainHandItem()).ifPresent((state) -> {
            try {
                state.setOnClick(true);
                AttackHelper.attack(attacker, t, comboRatio);
            } finally {
                state.setOnClick(false);
            }
        }), target, forceHit, resetHit);

        ArrowReflector.doReflect(target, attacker);
        TNTExtinguisher.doExtinguishing(target, attacker);
    }

    public static void playQuickSheathSoundAction(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void playPiercingSoundAction(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static Vec3 genRushOffset(LivingEntity entityIn) {
        return new Vec3(entityIn.getRandom().nextFloat() - 0.5f, entityIn.getRandom().nextFloat() - 0.5f, 0).scale(2.0);
    }

    public static float getSlashBladeDamageScale(LivingEntity entity) {
        AttributeInstance attributeInstance = entity.getAttribute(ModAttributes.getSlashBladeDamage());
        if (attributeInstance != null) {
            return (float) attributeInstance.getValue();
        }
        return 1.0f;
    }

}