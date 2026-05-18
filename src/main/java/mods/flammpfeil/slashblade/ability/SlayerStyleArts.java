package mods.flammpfeil.slashblade.ability;

import cn.sh1rocu.slashblade.api.event.PlayerTickEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.mixin.accessor.ServerPlayerAccessor;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.event.ability.SprintMoveEvent;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.NBTHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class SlayerStyleArts {

    private static final class SingletonHolder {

        private static final SlayerStyleArts INSTANCE = new SlayerStyleArts();
    }

    public static SlayerStyleArts getInstance() {
        return SlayerStyleArts.SingletonHolder.INSTANCE;
    }

    private SlayerStyleArts() {
    }

    public void register() {
        InputCommandEvent.CALLBACK.register(this::onInputChange);
        this.onTick();
    }


    final static EnumSet<InputCommand> FORWARD_SPRINT_SNEAK_COMMAND = EnumSet.of(
            InputCommand.FORWARD,
            InputCommand.SPRINT,
            InputCommand.SNEAK
    );
    final static EnumSet<InputCommand> BACK_SPRINT_SNEAK_COMMAND = EnumSet.of(
            InputCommand.BACK,
            InputCommand.SPRINT,
            InputCommand.SNEAK
    );
    final static EnumSet<InputCommand> MOVE_COMMAND = EnumSet.of(
            InputCommand.FORWARD,
            InputCommand.BACK,
            InputCommand.LEFT,
            InputCommand.RIGHT
    );

    public static final Vec3 BACK_MOTION = new Vec3(0, -5, 0);

    static public final ResourceLocation ADVANCEMENT_AIR_TRICK = SlashBlade.prefix("abilities/air_trick");
    static public final ResourceLocation ADVANCEMENT_TRICK_DOWN = SlashBlade.prefix("abilities/trick_down");
    static public final ResourceLocation ADVANCEMENT_TRICK_DODGE = SlashBlade.prefix("abilities/trick_dodge");
    static public final ResourceLocation ADVANCEMENT_TRICK_UP = SlashBlade.prefix("abilities/trick_up");

    public static final String AVOID_TRICKUP_PATH = "sb.avoid.trickup";
    public static final String AVOID_COUNTER_PATH = "sb.avoid.counter";
    public static final String AVOID_VEC_PATH = "sb.avoid.vec";
    public static final String AIRTRICK_COUNTER_PATH = "sb.airtrick.counter";
    public static final String AIRTRICK_TARGET_PATH = "sb.airtrick.target";

    public static final String STORE_STEPUP_PATH = "sb.store.stepup";
    public static final String TMP_STEPUP_PATH = "sb.tmp.stepup";
    public static final String DO_FORCE_HIT_PATH = "doForceHit";

    public final static int TRICK_ACTION_UNTOUCHABLE_TIME = 10;

    public void onInputChange(InputCommandEvent event) {
        ServerPlayer sender = event.getEntity();
        ItemStack stack = sender.getMainHandItem();

        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;

        ServerLevel worldIn = sender.serverLevel();
        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();

        if (!old.contains(InputCommand.SPRINT)) {
            processInputCommands(sender, worldIn, current);

        }
    }

    public void processInputCommands(ServerPlayer sender, Level worldIn, EnumSet<InputCommand> current) {
        boolean isHandled = false;

        if (current.containsAll(FORWARD_SPRINT_SNEAK_COMMAND)) {
            isHandled = handleForwardSprintSneak(sender, worldIn);

        }

        if (!isHandled && !sender.onGround() &&
                current.containsAll(BACK_SPRINT_SNEAK_COMMAND)) {
            isHandled = handleBackSprintSneak(sender);

        }

        if (!isHandled && sender.onGround() &&
                current.contains(InputCommand.SPRINT)
                && current.stream().anyMatch(MOVE_COMMAND::contains)) {
            SprintMoveEvent sprintEvent = new SprintMoveEvent(sender, current);
            SprintMoveEvent.EVENT.invoker().post(sprintEvent);

            if (!sprintEvent.isCanceled()) {
                handleSprintMove(sender, current);
            }

        }
    }

    public boolean handleForwardSprintSneak(ServerPlayer sender, Level worldIn) {
        return CapabilitySlashBlade.BLADESTATE.maybeGet(sender.getMainHandItem()).map(state -> {
            Entity tmpTarget = state.getTargetEntity(worldIn);
            Entity target = (tmpTarget instanceof MultiPartEntity mp && mp.getParts() != null && mp.getParts().length > 0)
                    ? mp.getParts()[0] : tmpTarget;

            if (target == null
                    && ((EntityExtension) sender).sb$getPersistentData().getInt(AVOID_TRICKUP_PATH) == 0) {
                return executeTrickUp(sender);
            } else if (target != null) {
                return executeAirTrick(sender, worldIn, target, state);
            }

            return false;
        }).orElse(false);
    }

    public boolean handleBackSprintSneak(ServerPlayer sender) {
        Vec3 oldPos = sender.position();

        sender.move(MoverType.SELF, BACK_MOTION);
        if (sender.onGround()) {
            applyFullTrickEffects(sender, BACK_MOTION, AVOID_COUNTER_PATH, ADVANCEMENT_TRICK_DOWN, 0.75f);

            return true;
        } else {
            sender.setPos(oldPos);

            return false;
        }
    }

    public boolean handleSprintMove(ServerPlayer sender, EnumSet<InputCommand> current) {
        ServerLevel level = sender.serverLevel();
        int count = CapabilityMobEffect.MOB_EFFECT.maybeGet(sender)
                .map(effect -> effect.doAvoid(level.getGameTime()))
                .orElse(0);

        if (count > 0) {
            applyBasicTrickEffects(sender);

            Vec3 input = getInput(current);
            sender.moveRelative(3.0f, input);
            Vec3 motion = this.maybeBackOffFromEdge(sender.getDeltaMovement(), sender);

            sender.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.2f);
            sender.move(MoverType.SELF, motion);
            sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(0.5f)));

            applyTrickMotionAndData(sender, motion, AVOID_COUNTER_PATH, sender.position());
            AdvancementHelper.grantCriterion(sender, ADVANCEMENT_TRICK_DODGE);

            CapabilitySlashBlade.BLADESTATE.maybeGet(sender.getMainHandItem())
                    .ifPresent(state -> state.updateComboSeq(sender, state.getComboRoot()));

        }

        return true;
    }

    public static @NotNull Vec3 getInput(EnumSet<InputCommand> current) {
        float moveForward = current.contains(InputCommand.FORWARD) ^ current.contains(InputCommand.BACK)
                ?
                (current.contains(InputCommand.FORWARD) ?
                 1.0F :
                 -1.0F) :
                0.0F;

        float moveStrafe = current.contains(InputCommand.LEFT) ^ current.contains(InputCommand.RIGHT)
                ?
                (current.contains(InputCommand.LEFT) ?
                 1.0F :
                 -1.0F) :
                0.0F;

        return new Vec3(moveStrafe, 0, moveForward);
    }

    public void applyBasicTrickEffects(ServerPlayer sender) {
        Untouchable.setUntouchable(sender, TRICK_ACTION_UNTOUCHABLE_TIME);
        ((ServerPlayerAccessor) sender).sb$setChangingDimension(true);

    }

    public void applyTrickMotionAndData(ServerPlayer sender, Vec3 motion, String counterPath, Vec3 position) {
        sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(0.75f)));
        ((EntityExtension) sender).sb$getPersistentData().putInt(counterPath, 2);
        NBTHelper.putVector3d(((EntityExtension) sender).sb$getPersistentData(), AVOID_VEC_PATH, position);

    }

    public void applyTrickSoundAndAdvancement(ServerPlayer sender, ResourceLocation advancement) {
        AdvancementHelper.grantCriterion(sender, advancement);
        sender.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.2f);

    }

    public void applyFullTrickEffects(ServerPlayer sender, Vec3 motion, String counterPath,
                                      ResourceLocation advancement, float motionScale) {
        applyBasicTrickEffects(sender);
        sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(motionScale)));
        ((EntityExtension) sender).sb$getPersistentData().putInt(counterPath, 2);
        NBTHelper.putVector3d(((EntityExtension) sender).sb$getPersistentData(), AVOID_VEC_PATH, sender.position());
        applyTrickSoundAndAdvancement(sender, advancement);

    }

    public boolean executeTrickUp(ServerPlayer sender) {
        Vec3 motion = new Vec3(0, +0.8, 0);
        sender.move(MoverType.SELF, motion);

        applyFullTrickEffects(sender, motion, AVOID_TRICKUP_PATH, ADVANCEMENT_TRICK_UP, 0.75f);
        ((EntityExtension) sender).sb$getPersistentData().putInt(AVOID_COUNTER_PATH, 2);
        sender.setOnGround(false);

        return true;
    }

    public boolean executeAirTrick(ServerPlayer sender, Level worldIn, Entity target, ISlashBladeState state) {
        if (target == sender.getLastHurtMob() && sender.tickCount < sender.getLastHurtMobTimestamp() + 100) {
            LivingEntity hitEntity = sender.getLastHurtMob();
            if (hitEntity != null) {
                SlayerStyleArts.doTeleport(sender, hitEntity);

            }
        } else {
            createSummonedSwordForAirTrick(sender, worldIn, target, state);

        }
        return true;
    }

    public void createSummonedSwordForAirTrick(ServerPlayer sender, Level worldIn, Entity target, ISlashBladeState state) {
        EntityAbstractSummonedSword ss = new EntityAbstractSummonedSword(
                SBEntityTypes.SUMMONED_SWORD, worldIn) {

            @Override
            protected void onHitEntity(EntityHitResult entityHitResult) {
                super.onHitEntity(entityHitResult);
                LivingEntity hitTarget = sender.getLastHurtMob();
                if (hitTarget != null && this.getHitEntity() == hitTarget) {
                    SlayerStyleArts.doTeleport(sender, hitTarget);

                }
            }

            @Override
            public void tick() {
                if (this.sb$getPersistentData().getBoolean(DO_FORCE_HIT_PATH)) {
                    this.doForceHitEntity(target);
                    this.sb$getPersistentData().remove(DO_FORCE_HIT_PATH);

                }
                super.tick();
            }
        };

        Vec3 lastPos = sender.getEyePosition(1.0f);
        ss.xOld = lastPos.x;
        ss.yOld = lastPos.y;
        ss.zOld = lastPos.z;

        Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2.0, 0)
                .add(sender.getLookAngle().scale(-2.0));
        ss.setPos(targetPos.x, targetPos.y, targetPos.z);

        Vec3 dir = sender.getLookAngle();
        ss.shoot(dir.x, dir.y, dir.z, 1.0f, 0);
        ss.setOwner(sender);
        ss.setDamage(0.01f);
        ss.setColor(state.getColorCode());
        ss.sb$getPersistentData().putBoolean(DO_FORCE_HIT_PATH, true);

        worldIn.addFreshEntity(ss);
        sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);

    }

    public static void doTeleport(Entity entityIn, LivingEntity target) {
        ((EntityExtension) entityIn).sb$getPersistentData().putInt(AIRTRICK_COUNTER_PATH, 3);
        ((EntityExtension) entityIn).sb$getPersistentData().putInt(AIRTRICK_TARGET_PATH, target.getId());

        if (entityIn instanceof ServerPlayer serverPlayer) {
            AdvancementHelper.grantCriterion(serverPlayer, ADVANCEMENT_AIR_TRICK);
            Vec3 motion = target.getPosition(1.0f).subtract(entityIn.getPosition(1.0f)).scale(0.5f);
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entityIn.getId(), motion));

        }
    }

    public static void executeTeleport(Entity entityIn, LivingEntity target) {
        if (!(entityIn.level() instanceof ServerLevel serverLevel)) return;

        prepareTeleportEffects(entityIn);
        Vec3 teleportPos = calculateTeleportPosition(entityIn, target);

        if (!isValidTeleportPosition(teleportPos)) {
            return;
        }

        performTeleportation(entityIn, serverLevel, teleportPos);
        applyPostTeleportEffects(entityIn);

    }

    public static void prepareTeleportEffects(Entity entityIn) {
        if (entityIn instanceof ServerPlayer serverPlayer) {
            serverPlayer.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75F, 1.25F);
            CapabilitySlashBlade.BLADESTATE.maybeGet(serverPlayer.getMainHandItem())
                    .ifPresent(state -> state.updateComboSeq(serverPlayer, state.getComboRoot()));
            Untouchable.setUntouchable(serverPlayer, TRICK_ACTION_UNTOUCHABLE_TIME);
        }
    }

    public static Vec3 calculateTeleportPosition(Entity entityIn, LivingEntity target) {
        return target.position()
                .add(0, target.getBbHeight() / 2.0, 0)
                .add(entityIn.getLookAngle().scale(-2.0));
    }

    public static boolean isValidTeleportPosition(Vec3 teleportPos) {
        BlockPos blockPos = new BlockPos((int) teleportPos.x, (int) teleportPos.y, (int) teleportPos.z);
        return Level.isInSpawnableBounds(blockPos);
    }

    public static void performTeleportation(Entity entityIn, ServerLevel serverLevel, Vec3 teleportPos) {
        double x = teleportPos.x;
        double y = teleportPos.y;
        double z = teleportPos.z;
        float yaw = entityIn.getYRot();
        float pitch = entityIn.getXRot();

        if (entityIn instanceof ServerPlayer serverPlayer) {
            handleServerPlayerTeleportation(serverPlayer, serverLevel, x, y, z, yaw, pitch);
        } else {
            handleEntityTeleportation(entityIn, serverLevel, x, y, z, yaw, pitch);
        }
    }

    public static void handleServerPlayerTeleportation(ServerPlayer serverPlayer, ServerLevel serverLevel,
                                                       double x, double y, double z, float yaw, float pitch) {
        Set<RelativeMovement> relativeList = Collections.emptySet();
        BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
        ChunkPos chunkPos = new ChunkPos(blockPos);

        serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, serverPlayer.getId());
        serverPlayer.stopRiding();

        if (serverPlayer.isSleeping()) {
            serverPlayer.stopSleepInBed(true, true);

        }

        if (serverLevel == serverPlayer.level()) {
            serverPlayer.connection.teleport(x, y, z, yaw, pitch, relativeList);

        } else {
            serverPlayer.teleportTo(serverLevel, x, y, z, yaw, pitch);

        }
        serverPlayer.setYHeadRot(yaw);

    }

    public static void handleEntityTeleportation(Entity entityIn, ServerLevel serverLevel,
                                                 double x, double y, double z, float yaw, float pitch) {
        float wrappedYaw = Mth.wrapDegrees(yaw);
        float clampedPitch = Mth.clamp(Mth.wrapDegrees(pitch), -90.0F, 90.0F);

        if (serverLevel == entityIn.level()) {
            entityIn.moveTo(x, y, z, wrappedYaw, clampedPitch);
            entityIn.setYHeadRot(wrappedYaw);
        } else {
            handleCrossDimensionTeleport(entityIn, serverLevel, x, y, z, wrappedYaw, clampedPitch);
        }
    }

    public static void handleCrossDimensionTeleport(Entity entityIn, ServerLevel serverLevel,
                                                    double x, double y, double z, float yaw, float pitch) {
        entityIn.unRide();
        Entity newEntity = entityIn.getType().create(serverLevel);

        if (newEntity != null) {
            newEntity.restoreFrom(entityIn);
            newEntity.moveTo(x, y, z, yaw, pitch);
            newEntity.setYHeadRot(yaw);
        }
    }

    public static void applyPostTeleportEffects(Entity entityIn) {
        if (!(entityIn instanceof LivingEntity) || !((LivingEntity) entityIn).isFallFlying()) {
            entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            entityIn.setOnGround(false);
        }

        if (entityIn instanceof PathfinderMob pathfinderMob) {
            pathfinderMob.getNavigation().stop();
        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 vec, LivingEntity mover) {
        double d0 = vec.x;
        double d1 = vec.z;

        while (d0 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(d0, -mover.maxUpStep(), 0.0D))) {
            if (d0 < 0.05D && d0 >= -0.05D) {
                d0 = 0.0D;
            } else if (d0 > 0.0D) {
                d0 -= 0.05D;
            } else {
                d0 += 0.05D;
            }
        }

        while (d1 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(0.0D, -mover.maxUpStep(), d1))) {
            if (d1 < 0.05D && d1 >= -0.05D) {
                d1 = 0.0D;
            } else if (d1 > 0.0D) {
                d1 -= 0.05D;
            } else {
                d1 += 0.05D;
            }
        }

        while (d0 != 0.0D && d1 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(d0, -mover.maxUpStep(), d1))) {
            if (d0 < 0.05D && d0 >= -0.05D) {
                d0 = 0.0D;
            } else if (d0 > 0.0D) {
                d0 -= 0.05D;
            } else {
                d0 += 0.05D;
            }

            if (d1 < 0.05D && d1 >= -0.05D) {
                d1 = 0.0D;
            } else if (d1 > 0.0D) {
                d1 -= 0.05D;
            } else {
                d1 += 0.05D;
            }
        }

        vec = new Vec3(d0, vec.y, d1);

        return vec;
    }

    static final float stepUpBoost = 1.1f;
    static final float stepUpDefault = 0.6f;

    public void onTick() {
        PlayerTickEvent.START.register(this::handleTickStart);
        PlayerTickEvent.END.register(this::handleTickEnd);
    }

    public void handleTickStart(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        handleStepUpBoost(player);
        handleTrickUpCooldown(player);
        handleAvoidCounter(player);
        handleAirTrickCounter(player);

    }

    public void handleStepUpBoost(Player player) {
        float stepUp = player.maxUpStep();
        boolean doStepupBoost = shouldApplyStepUpBoost(player);

        if (doStepupBoost && (player.getMainHandItem().getItem() instanceof ItemSlashBlade)
                && stepUp < stepUpBoost) {
            ((EntityExtension) player).sb$getPersistentData().putFloat(STORE_STEPUP_PATH, stepUp);
            player.setMaxUpStep(stepUpBoost);

        }
    }


    public boolean shouldApplyStepUpBoost(Player player) {
        Vec3 deltaMovement = calculatePlayerMovement(player);

        if (deltaMovement.equals(Vec3.ZERO)) {
            return false;
        }

        Vec3 offset = deltaMovement.normalize().scale(0.5f).add(0, 0.25, 0);
        BlockPos offsetPos = new BlockPos(VectorHelper.f2i(player.position().add(offset))).below();
        BlockState blockState = player.level().getBlockState(offsetPos);
        @SuppressWarnings("deprecation")
        var liquid = !blockState.liquid();
        return liquid;
    }

    public Vec3 calculatePlayerMovement(Player player) {
        Vec3 input = new Vec3(player.xxa, player.yya, player.zza);
        double scale = 1.0;
        float yRot = player.getYRot();
        double d0 = input.lengthSqr();

        if (d0 < 1.0E-7D) {
            return Vec3.ZERO;
        }

        Vec3 vec3 = (d0 > 1.0D ? input.normalize() : input).scale(scale);
        float f = Mth.sin(yRot * ((float) Math.PI / 180F));
        float f1 = Mth.cos(yRot * ((float) Math.PI / 180F));

        return new Vec3(vec3.x * f1 - vec3.z * f, vec3.y, vec3.z * f1 + vec3.x * f);
    }

    public void handleTrickUpCooldown(Player player) {
        if (!player.onGround() || !((EntityExtension) player).sb$getPersistentData().contains(AVOID_TRICKUP_PATH)) {
            return;
        }

        int count = ((EntityExtension) player).sb$getPersistentData().getInt(AVOID_TRICKUP_PATH) - 1;

        if (count <= 0) {
            ((EntityExtension) player).sb$getPersistentData().remove(AVOID_TRICKUP_PATH);
            triggerDimensionChange(player);
        } else {
            ((EntityExtension) player).sb$getPersistentData().putInt(AVOID_TRICKUP_PATH, count);
        }
    }

    public void handleAvoidCounter(Player player) {
        if (!((EntityExtension) player).sb$getPersistentData().contains(AVOID_COUNTER_PATH)) {
            return;
        }

        int count = ((EntityExtension) player).sb$getPersistentData().getInt(AVOID_COUNTER_PATH) - 1;

        if (count <= 0) {
            restoreAvoidPosition(player);
            clearAvoidData(player);
            triggerDimensionChange(player);

        } else {
            ((EntityExtension) player).sb$getPersistentData().putInt(AVOID_COUNTER_PATH, count);

        }
    }

    public void restoreAvoidPosition(Player player) {
        if (((EntityExtension) player).sb$getPersistentData().contains(AVOID_VEC_PATH)) {
            Vec3 pos = NBTHelper.getVector3d(((EntityExtension) player).sb$getPersistentData(), AVOID_VEC_PATH);
            player.moveTo(pos);

        }
    }

    public void clearAvoidData(Player player) {
        ((EntityExtension) player).sb$getPersistentData().remove(AVOID_COUNTER_PATH);
        ((EntityExtension) player).sb$getPersistentData().remove(AVOID_VEC_PATH);

    }

    public void handleAirTrickCounter(Player player) {
        if (!((EntityExtension) player).sb$getPersistentData().contains(AIRTRICK_COUNTER_PATH)) {
            return;
        }

        int count = ((EntityExtension) player).sb$getPersistentData().getInt(AIRTRICK_COUNTER_PATH) - 1;

        if (count <= 0) {
            executeAirTrickTeleport(player);
            clearAirTrickData(player);
            triggerDimensionChange(player);

        } else {
            ((EntityExtension) player).sb$getPersistentData().putInt(AIRTRICK_COUNTER_PATH, count);

        }
    }

    public void executeAirTrickTeleport(Player player) {
        if (!((EntityExtension) player).sb$getPersistentData().contains(AIRTRICK_TARGET_PATH)) {
            return;
        }

        int id = ((EntityExtension) player).sb$getPersistentData().getInt(AIRTRICK_TARGET_PATH);
        Entity target = player.level().getEntity(id);

        if (target instanceof LivingEntity livingEntity) {
            executeTeleport(player, livingEntity);

        }
    }

    public void clearAirTrickData(Player player) {
        ((EntityExtension) player).sb$getPersistentData().remove(AIRTRICK_COUNTER_PATH);
        ((EntityExtension) player).sb$getPersistentData().remove(AIRTRICK_TARGET_PATH);

    }

    public void triggerDimensionChange(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.hasChangedDimension();

        }
    }

    public void handleTickEnd(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        float stepUp = ((EntityExtension) player).sb$getPersistentData().getFloat(TMP_STEPUP_PATH);
        stepUp = Math.max(stepUp, stepUpDefault);

        if (stepUp < player.maxUpStep()) {
            player.setMaxUpStep(stepUp);

        }
    }

}