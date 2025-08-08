package mods.flammpfeil.slashblade.ability;

import cn.sh1rocu.slashblade.api.event.PlayerTickEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.mixin.accessor.ServerPlayerAccessor;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class SlayerStyleArts {
    private static final class SingletonHolder {
        private static final SlayerStyleArts instance = new SlayerStyleArts();
    }

    public static SlayerStyleArts getInstance() {
        return SingletonHolder.instance;
    }

    private SlayerStyleArts() {
    }

    public void register() {
        InputCommandEvent.CALLBACK.register(this::onInputChange);
        PlayerTickEvent.START.register(this::onTickStart);
        PlayerTickEvent.END.register(this::onTickEnd);
    }

    final static EnumSet<InputCommand> fowerd_sprint_sneak = EnumSet.of(InputCommand.FORWARD, InputCommand.SPRINT,
            InputCommand.SNEAK);
    final static EnumSet<InputCommand> back_sprint_sneak = EnumSet.of(InputCommand.BACK, InputCommand.SPRINT,
            InputCommand.SNEAK);
    final static EnumSet<InputCommand> move = EnumSet.of(InputCommand.FORWARD, InputCommand.BACK, InputCommand.LEFT,
            InputCommand.RIGHT);

    public static final ResourceLocation ADVANCEMENT_AIR_TRICK = new ResourceLocation(SlashBlade.MODID,
            "abilities/air_trick");
    public static final ResourceLocation ADVANCEMENT_TRICK_DOWN = new ResourceLocation(SlashBlade.MODID,
            "abilities/trick_down");
    public static final ResourceLocation ADVANCEMENT_TRICK_DODGE = new ResourceLocation(SlashBlade.MODID,
            "abilities/trick_dodge");
    public static final ResourceLocation ADVANCEMENT_TRICK_UP = new ResourceLocation(SlashBlade.MODID,
            "abilities/trick_up");

    final static int TRICKACTION_UNTOUCHABLE_TIME = 10;

    public void onInputChange(InputCommandEvent event) {

        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();
        Level worldIn = sender.level();

        ItemStack stack = sender.getMainHandItem();
        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        if (!old.contains(InputCommand.SPRINT)) {

            boolean isHandled = false;

            if (current.containsAll(fowerd_sprint_sneak)) {
                // air trick Or trick up
                isHandled = CapabilitySlashBlade.BLADESTATE.maybeGet(sender.getMainHandItem()).map(state -> {
                    Entity tmpTarget = state.getTargetEntity(worldIn);

                    Entity target;

                    //if (tmpTarget != null && tmpTarget.getParts() != null && tmpTarget.getParts().length > 0) {
                    //    target = tmpTarget.getParts()[0];
                    //} else {
                    target = tmpTarget;
                    //}

                    if (target == null && 0 == ((EntityExtension) sender).sb$getPersistentData().getInt("sb.avoid.trickup")) {
                        // trick up
                        Untouchable.setUntouchable(sender, TRICKACTION_UNTOUCHABLE_TIME);

                        Vec3 motion = new Vec3(0, +0.8, 0);

                        sender.move(MoverType.SELF, motion);
                        ((ServerPlayerAccessor) sender).sb$setChangingDimension(true);

                        sender.connection
                                .send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(0.75f)));

                        ((EntityExtension) sender).sb$getPersistentData().putInt("sb.avoid.trickup", 2);
                        sender.setOnGround(false);

                        ((EntityExtension) sender).sb$getPersistentData().putInt("sb.avoid.counter", 2);
                        NBTHelper.putVector3d(((EntityExtension) sender).sb$getPersistentData(), "sb.avoid.vec", sender.position());

                        AdvancementHelper.grantCriterion(sender, ADVANCEMENT_TRICK_UP);
                        sender.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.2f);

                        return true;
                    } else if (target != null) {
                        // air trick
                        if (target == sender.getLastHurtMob()
                                && sender.tickCount < sender.getLastHurtMobTimestamp() + 100) {
                            LivingEntity hitEntity = sender.getLastHurtMob();
                            if (hitEntity != null) {
                                SlayerStyleArts.doTeleport(sender, hitEntity);
                            }
                        } else {
                            EntityAbstractSummonedSword ss = new EntityAbstractSummonedSword(
                                    SBEntityTypes.SummonedSword, worldIn) {
                                @Override
                                protected void onHitEntity(EntityHitResult entityHitResult) {
                                    super.onHitEntity(entityHitResult);

                                    LivingEntity target = sender.getLastHurtMob();
                                    if (target != null && this.getHitEntity() == target) {
                                        SlayerStyleArts.doTeleport(sender, target);
                                    }
                                }

                                @Override
                                public void tick() {
                                    if (this.sb$getPersistentData().getBoolean("doForceHit")) {
                                        this.doForceHitEntity(target);
                                        this.sb$getPersistentData().remove("doForceHit");
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

                            ss.sb$getPersistentData().putBoolean("doForceHit", true);

                            worldIn.addFreshEntity(ss);
                            sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);

                            // ss.doForceHitEntity(target);
                        }
                        return true;
                    }

                    return false;

                }).orElse(false);
            }

            // trick down
            if (!isHandled && !sender.onGround() && current.containsAll(back_sprint_sneak)) {
                Vec3 oldpos = sender.position();

                Vec3 motion = new Vec3(0, -5, 0);

                sender.move(MoverType.SELF, motion);
                if (sender.onGround()) {
                    Untouchable.setUntouchable(sender, TRICKACTION_UNTOUCHABLE_TIME);

                    ((ServerPlayerAccessor) sender).sb$setChangingDimension(true);

                    sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(0.75f)));

                    ((EntityExtension) sender).sb$getPersistentData().putInt("sb.avoid.counter", 2);
                    NBTHelper.putVector3d(((EntityExtension) sender).sb$getPersistentData(), "sb.avoid.vec", sender.position());

                    AdvancementHelper.grantCriterion(sender, ADVANCEMENT_TRICK_DOWN);
                    sender.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.2f);

                    isHandled = true;
                } else {
                    sender.setPos(oldpos);
                }

            }

            if (!isHandled && sender.onGround() && current.contains(InputCommand.SPRINT)
                    && current.stream().anyMatch(move::contains)) {
                // quick avoid ground
                Level level = sender.level();
                int count = CapabilityMobEffect.MOB_EFFECT.maybeGet(sender)
                        .map(ef -> ef.doAvoid(level.getGameTime())).orElse(0);

                if (0 < count) {
                    Untouchable.setUntouchable(sender, TRICKACTION_UNTOUCHABLE_TIME);

                    float moveForward = current.contains(InputCommand.FORWARD) == current.contains(InputCommand.BACK)
                            ? 0.0F
                            : (current.contains(InputCommand.FORWARD) ? 1.0F : -1.0F);
                    float moveStrafe = current.contains(InputCommand.LEFT) == current.contains(InputCommand.RIGHT)
                            ? 0.0F
                            : (current.contains(InputCommand.LEFT) ? 1.0F : -1.0F);
                    Vec3 input = new Vec3(moveStrafe, 0, moveForward);

                    sender.moveRelative(3.0f, input);

                    Vec3 motion = this.maybeBackOffFromEdge(sender.getDeltaMovement(), sender);

                    sender.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.2f);

                    sender.move(MoverType.SELF, motion);
                    ((ServerPlayerAccessor) sender).sb$setChangingDimension(true);

                    // sender.moveTo(sender.position());

                    sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), motion.scale(0.5f)));

                    ((EntityExtension) sender).sb$getPersistentData().putInt("sb.avoid.counter", 2);
                    NBTHelper.putVector3d(((EntityExtension) sender).sb$getPersistentData(), "sb.avoid.vec", sender.position());

                    AdvancementHelper.grantCriterion(sender, ADVANCEMENT_TRICK_DODGE);

                    CapabilitySlashBlade.BLADESTATE.maybeGet(sender.getMainHandItem())
                            .ifPresent(state -> state.updateComboSeq(sender, state.getComboRoot()));
                }

                isHandled = true;
            }
            // slow avoid ground
            // move double tap

            /**
             * //relativeList : pos -> convertflag -> motion
             * sender.connection.setPlayerLocation(sender.getPosX(), sender.getPosY(),
             * sender.getPosZ() , sender.getYaw(1.0f), sender.getPitch(1.0f) ,
             * Sets.newHashSet(SPlayerPositionLookPacket.Flags.X,SPlayerPositionLookPacket.Flags.Z));
             */
        }

    }

    private static void doTeleport(Entity entityIn, LivingEntity target) {
        ((EntityExtension) entityIn).sb$getPersistentData().putInt("sb.airtrick.counter", 3);
        ((EntityExtension) entityIn).sb$getPersistentData().putInt("sb.airtrick.target", target.getId());

        if (entityIn instanceof ServerPlayer) {
            AdvancementHelper.grantCriterion((ServerPlayer) entityIn, ADVANCEMENT_AIR_TRICK);
            Vec3 motion = target.getPosition(1.0f).subtract(entityIn.getPosition(1.0f)).scale(0.5f);
            ((ServerPlayer) entityIn).connection.send(new ClientboundSetEntityMotionPacket(entityIn.getId(), motion));
        }
    }

    private static void executeTeleport(Entity entityIn, LivingEntity target) {
        if (!(entityIn.level() instanceof ServerLevel))
            return;

        if (entityIn instanceof Player player) {
            player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75F, 1.25F);

            CapabilitySlashBlade.BLADESTATE.maybeGet(player.getMainHandItem())
                    .ifPresent(state -> state.updateComboSeq(player, state.getComboRoot()));

            Untouchable.setUntouchable(player, TRICKACTION_UNTOUCHABLE_TIME);
        }

        ServerLevel worldIn = (ServerLevel) entityIn.level();

        Vec3 tereportPos = target.position().add(0, target.getBbHeight() / 2.0, 0)
                .add(entityIn.getLookAngle().scale(-2.0));

        double x = tereportPos.x;
        double y = tereportPos.y;
        double z = tereportPos.z;
        float yaw = entityIn.getYRot();
        float pitch = entityIn.getXRot();

        Set<RelativeMovement> relativeList = Collections.emptySet();
        BlockPos blockpos = new BlockPos((int) x, (int) y, (int) z);
        if (!Level.isInSpawnableBounds(blockpos)) {
            return;
        } else {
            if (entityIn instanceof ServerPlayer serverPlayer) {
                ChunkPos chunkpos = new ChunkPos(blockpos);
                worldIn.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entityIn.getId());
                entityIn.stopRiding();
                if (serverPlayer.isSleeping()) {
                    serverPlayer.stopSleepInBed(true, true);
                }

                if (worldIn == entityIn.level()) {
                    serverPlayer.connection.teleport(x, y, z, yaw, pitch, relativeList);
                } else {
                    serverPlayer.teleportTo(worldIn, x, y, z, yaw, pitch);
                }

                entityIn.setYHeadRot(yaw);
            } else {
                float f1 = Mth.wrapDegrees(yaw);
                float f = Mth.wrapDegrees(pitch);
                f = Mth.clamp(f, -90.0F, 90.0F);
                if (worldIn == entityIn.level()) {
                    entityIn.moveTo(x, y, z, f1, f);
                    entityIn.setYHeadRot(f1);
                } else {
                    entityIn.unRide();
                    Entity entity = entityIn;
                    entityIn = entityIn.getType().create(worldIn);
                    if (entityIn == null) {
                        return;
                    }

                    entityIn.restoreFrom(entity);
                    entityIn.moveTo(x, y, z, f1, f);
                    entityIn.setYHeadRot(f1);
                    // worldIn.addFromAnotherDimension(entityIn);
                }
            }

            if (!(entityIn instanceof LivingEntity) || !((LivingEntity) entityIn).isFallFlying()) {
                entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
                entityIn.setOnGround(false);
            }

            if (entityIn instanceof PathfinderMob) {
                ((PathfinderMob) entityIn).getNavigation().stop();
            }

        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 vec, LivingEntity mover) {
        double d0 = vec.x;
        double d1 = vec.z;

        while (d0 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(d0, (double) (-mover.maxUpStep()), 0.0D))) {
            if (d0 < 0.05D && d0 >= -0.05D) {
                d0 = 0.0D;
            } else if (d0 > 0.0D) {
                d0 -= 0.05D;
            } else {
                d0 += 0.05D;
            }
        }

        while (d1 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(0.0D, (double) (-mover.maxUpStep()), d1))) {
            if (d1 < 0.05D && d1 >= -0.05D) {
                d1 = 0.0D;
            } else if (d1 > 0.0D) {
                d1 -= 0.05D;
            } else {
                d1 += 0.05D;
            }
        }

        while (d0 != 0.0D && d1 != 0.0D && mover.level().noCollision(mover,
                mover.getBoundingBox().move(d0, (double) (-mover.maxUpStep()), d1))) {
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

    @SuppressWarnings("deprecation")
    public void onTickStart(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        float stepUp = player.maxUpStep();

        Vec3 deltaMovement;

        Vec3 input = new Vec3(player.xxa, player.yya, player.zza);
        double scale = 1.0;
        float yRot = player.getYRot();
        double d0 = input.lengthSqr();
        if (d0 < 1.0E-7D) {
            deltaMovement = Vec3.ZERO;
        } else {
            Vec3 vec3 = (d0 > 1.0D ? input.normalize() : input).scale(scale);
            float f = Mth.sin(yRot * ((float) Math.PI / 180F));
            float f1 = Mth.cos(yRot * ((float) Math.PI / 180F));
            deltaMovement = new Vec3(vec3.x * (double) f1 - vec3.z * (double) f, vec3.y,
                    vec3.z * (double) f1 + vec3.x * (double) f);
        }


        boolean doStepupBoost = true;

        if (doStepupBoost) {
            Vec3 offset = deltaMovement.normalize().scale(0.5f).add(0, 0.25, 0);
            BlockPos offsetedPos = new BlockPos(VectorHelper.f2i(player.position().add(offset))).below();
            BlockState blockState = player.level().getBlockState(offsetedPos);
            if (blockState.liquid()) {
                doStepupBoost = false;
            }
        }

        if (doStepupBoost && (player.getMainHandItem().getItem() instanceof ItemSlashBlade)
                && stepUp < stepUpBoost) {
            ((EntityExtension) player).sb$getPersistentData().putFloat("sb.store.stepup", stepUp);
            player.setMaxUpStep(stepUpBoost);
        }

        // trick up cooldown
        if (player.onGround() && 0 < ((EntityExtension) player).sb$getPersistentData().getInt("sb.avoid.trickup")) {

            int count = ((EntityExtension) player).sb$getPersistentData().getInt("sb.avoid.trickup");
            count--;

            if (count <= 0) {
                ((EntityExtension) player).sb$getPersistentData().remove("sb.avoid.trickup");

                if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).hasChangedDimension();
                }
            } else {
                ((EntityExtension) player).sb$getPersistentData().putInt("sb.avoid.trickup", count);
            }
        }

        // handle avoid
        if (((EntityExtension) player).sb$getPersistentData().contains("sb.avoid.counter")) {
            int count = ((EntityExtension) player).sb$getPersistentData().getInt("sb.avoid.counter");
            count--;

            if (count <= 0) {
                if (((EntityExtension) player).sb$getPersistentData().contains("sb.avoid.vec")) {
                    Vec3 pos = NBTHelper.getVector3d(((EntityExtension) player).sb$getPersistentData(), "sb.avoid.vec");
                    player.moveTo(pos);
                }

                ((EntityExtension) player).sb$getPersistentData().remove("sb.avoid.counter");
                ((EntityExtension) player).sb$getPersistentData().remove("sb.avoid.vec");

                if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).hasChangedDimension();
                }
            } else {
                ((EntityExtension) player).sb$getPersistentData().putInt("sb.avoid.counter", count);
            }
        }

        // handle AirTrick
        if (((EntityExtension) player).sb$getPersistentData().contains("sb.airtrick.counter")) {
            int count = ((EntityExtension) player).sb$getPersistentData().getInt("sb.airtrick.counter");
            count--;

            if (count <= 0) {
                if (((EntityExtension) player).sb$getPersistentData().contains("sb.airtrick.target")) {
                    int id = ((EntityExtension) player).sb$getPersistentData().getInt("sb.airtrick.target");

                    Entity target = player.level().getEntity(id);
                    if (target != null && target instanceof LivingEntity)
                        executeTeleport(player, ((LivingEntity) target));
                }

                ((EntityExtension) player).sb$getPersistentData().remove("sb.airtrick.counter");
                ((EntityExtension) player).sb$getPersistentData().remove("sb.airtrick.target");
                if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).hasChangedDimension();
                }
            } else {
                ((EntityExtension) player).sb$getPersistentData().putInt("sb.airtrick.counter", count);
            }
        }
    }

    public void onTickEnd(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        float stepUp = ((EntityExtension) player).sb$getPersistentData().getFloat("sb.tmp.stepup");
        stepUp = Math.max(stepUp, stepUpDefault);

        if (stepUp < player.maxUpStep())
            player.setMaxUpStep(stepUp);
    }
}
