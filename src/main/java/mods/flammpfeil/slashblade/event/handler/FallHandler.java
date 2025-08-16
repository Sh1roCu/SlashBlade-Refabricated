package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.LivingFallEvent;
import cn.sh1rocu.slashblade.api.event.PlayerFlyableFallEvent;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FallHandler {
    private static final class SingletonHolder {
        private static final FallHandler instance = new FallHandler();
    }

    public static FallHandler getInstance() {
        return SingletonHolder.instance;
    }

    public void register() {
        LivingFallEvent.CALLBACK.register(this::onFall);
        PlayerFlyableFallEvent.CALLBACK.register(this::onFlyableFall);
    }

    public void onFall(LivingFallEvent event) {
        resetState(event.getEntity());
    }

    public void onFlyableFall(PlayerFlyableFallEvent event) {
        resetState(event.getEntity());
    }

    public static void resetState(LivingEntity user) {
        CapabilitySlashBlade.BLADESTATE.maybeGet(user.getMainHandItem()).ifPresent(
                state -> {
                    state.setFallDecreaseRate(0);

                    ComboState combo = ComboStateRegistry.COMBO_STATE.get(state.getComboSeq()) != null
                            ? ComboStateRegistry.COMBO_STATE.get(state.getComboSeq())
                            : ComboStateRegistry.NONE;
                    if (combo.isAerial()) {
                        state.setComboSeq(combo.getNextOfTimeout(user));
                    }
                });

    }

    public static void spawnLandingParticle(LivingEntity user, float fallFactor) {
        if (!user.level().isClientSide()) {
            int x = Mth.floor(user.getX());
            int y = Mth.floor(user.getY() - (double) 0.5F);
            int z = Mth.floor(user.getZ());
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = user.level().getBlockState(pos);

            float f = (float) Mth.ceil(fallFactor);
            if (!state.isAir()) {
                double d0 = Math.min(0.2F + f / 15.0F, 2.5D);
                int i = (int) (150.0D * d0);
                //if (!state.addLandingEffects((ServerLevel) user.level(), pos, state, user, i))
                ((ServerLevel) user.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        user.getX(), user.getY(), user.getZ(), i, 0.0D, 0.0D, 0.0D, 0.15F);
            }
        }
    }

    public static void spawnLandingParticle(Entity user, Vec3 targetPos, Vec3 normal, float fallFactor) {
        if (!user.level().isClientSide()) {

            Vec3 blockPos = targetPos.add(normal.normalize().scale(0.5f));

            int x = Mth.floor(blockPos.x());
            int y = Mth.floor(blockPos.y());
            int z = Mth.floor(blockPos.z());
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = user.level().getBlockState(pos);

            float f = (float) Mth.ceil(fallFactor);
            if (!state.isAir()) {
                double d0 = Math.min(0.2F + f / 15.0F, 2.5D);
                int i = (int) (150.0D * d0);
                ((ServerLevel) user.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        targetPos.x(), targetPos.y(), targetPos.z(), i, 0.0D, 0.0D, 0.0D, 0.15F);
            }
        }
    }

    public static void fallDecrease(LivingEntity user) {
        if (!user.isNoGravity() && !user.onGround()) {
            user.fallDistance = 1;

            float currentRatio = CapabilitySlashBlade.BLADESTATE.maybeGet(user.getMainHandItem()).map((state) -> {
                float decRatio = state.getFallDecreaseRate();

                float newDecRatio = decRatio + 0.05f;
                newDecRatio = Math.min(1.0f, newDecRatio);
                state.setFallDecreaseRate(newDecRatio);

                return decRatio;
            }).orElse(1.0f);

            double gravityReductionFactor = 0.85f;

            int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FALL_PROTECTION, user.getMainHandItem());
            if (level > 0) {
                gravityReductionFactor = Math.min(0.93, gravityReductionFactor + 0.02 * level);
                AdvancementHelper.grantedIf(Enchantments.FALL_PROTECTION, user);
            }

            // TODO
            // AttributeInstance gravity = user.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
            // double g = gravity.getValue() * gravityReductionFactor;

            // gravity.getValue()默认值为0.08
            double g = 0.08 * gravityReductionFactor;

            Vec3 motion = user.getDeltaMovement();
            if (motion.y < 0)
                user.setDeltaMovement(motion.x, (motion.y + g) * currentRatio, motion.z);
        }
    }

    public static void fallResist(LivingEntity user) {
        if (!user.isNoGravity() && !user.onGround()) {
            user.fallDistance = 1;

            Vec3 motion = user.getDeltaMovement();
            // TODO
            // AttributeInstance gravity = user.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
            // double g = gravity.getValue();

            // gravity.getValue()默认值为0.08
            double g = 0.08;
            if (motion.y < 0)
                user.setDeltaMovement(motion.x, (motion.y + g + 0.002f), motion.z);
        }
    }
}
