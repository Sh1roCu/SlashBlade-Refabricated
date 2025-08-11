package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.init.SBStatTypes;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SummonedSwordArts {
    private static final class SingletonHolder {
        private static final SummonedSwordArts instance = new SummonedSwordArts();
    }

    public static SummonedSwordArts getInstance() {
        return SingletonHolder.instance;
    }

    private SummonedSwordArts() {
    }

    public void register() {
        InputCommandEvent.CALLBACK.register(this::onInputChange);
    }

    public static final ResourceLocation ADVANCEMENT_SUMMONEDSWORDS = new ResourceLocation(SlashBlade.MODID,
            "arts/shooting/summonedswords");
    public static final ResourceLocation ADVANCEMENT_SPIRAL_SWORDS = new ResourceLocation(SlashBlade.MODID,
            "arts/shooting/spiral_swords");
    public static final ResourceLocation ADVANCEMENT_STORM_SWORDS = new ResourceLocation(SlashBlade.MODID,
            "arts/shooting/storm_swords");
    public static final ResourceLocation ADVANCEMENT_BLISTERING_SWORDS = new ResourceLocation(SlashBlade.MODID,
            "arts/shooting/blistering_swords");
    public static final ResourceLocation ADVANCEMENT_HEAVY_RAIN_SWORDS = new ResourceLocation(SlashBlade.MODID,
            "arts/shooting/heavy_rain_swords");

    public void onInputChange(InputCommandEvent event) {

        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();

        ItemStack blade = sender.getMainHandItem();
        var bladeState = CapabilitySlashBlade.BLADESTATE.maybeGet(blade).orElse(new SlashBladeState(blade));

        if (bladeState.isBroken() || bladeState.isSealed()
                || !SwordType.from(blade).contains(SwordType.BEWITCHED))
            return;

        int powerLevel = EnchantmentHelper
                .getItemEnchantmentLevel(Enchantments.POWER_ARROWS, blade);
        if (powerLevel <= 0)
            return;

        InputCommand targetCommand = InputCommand.M_DOWN;


        boolean onDown = !old.contains(targetCommand) && current.contains(targetCommand);

        final Long pressTime = event.getState().getLastPressTime(targetCommand);

        // basic summoned swords
        if (onDown) {

            CapabilityInputState.INPUT_STATE.maybeGet(sender).ifPresent(input -> {

                // SpiralSwords command
                input.getScheduler().schedule("SpiralSwords", pressTime + 10, (rawEntity, queue, now) -> {
                    if (!(rawEntity instanceof ServerPlayer))
                        return;
                    ServerPlayer entity = (ServerPlayer) rawEntity;

                    InputCommand targetCommnad = InputCommand.M_DOWN;
                    boolean inputSucceed = CapabilityInputState.INPUT_STATE.maybeGet(entity)
                            .filter(input3 -> input3.getCommands().contains(targetCommnad)
                                    && (!InputCommand.anyMatch(input3.getCommands(), InputCommand.move)
                                    || !input3.getCommands().contains(InputCommand.SNEAK))
                                    && input3.getLastPressTime(targetCommnad) == pressTime)
                            .isPresent();

                    if (!inputSucceed)
                        return;

                    // spiralSwords
                    boolean alreadySummoned = entity.getPassengers().stream()
                            .anyMatch(e -> e instanceof EntitySpiralSwords);

                    if (alreadySummoned) {
                        // fire
                        List<Entity> list = entity.getPassengers().stream()
                                .filter(e -> e instanceof EntitySpiralSwords).toList();

                        list.stream().forEach(e -> {
                            ((EntitySpiralSwords) e).doFire();
                        });
                    } else {
                        // summon
                        CapabilitySlashBlade.BLADESTATE.maybeGet(entity.getMainHandItem()).ifPresent((state) -> {

                            if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get())
                                return;
                            state.setProudSoulCount(
                                    state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

                            //圆环幻影剑
                            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_SPIRAL_SWORDS);

                            Level worldIn = entity.level();

                            int rank = CapabilityConcentrationRank.RANK_POINT.maybeGet(entity)
                                    .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

                            int count = 6;

                            if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                                count = 8;
                            }

                            for (int i = 0; i < count; i++) {
                                EntitySpiralSwords ss = new EntitySpiralSwords(
                                        SBEntityTypes.SpiralSwords, worldIn);
                                ss.setPos(entity.position());
                                ss.setOwner(entity);
                                ss.setColor(state.getColorCode());
                                ss.setRoll(0);
                                ss.setDamage(powerLevel);
                                // force riding
                                ss.startRiding(entity, true);

                                ss.setDelay(360 / count * i);

                                worldIn.addFreshEntity(ss);

                                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                                        1.45F);
                            }
                        });
                    }
                });

                // StormSwords command
                input.getScheduler().schedule("StormSwords", pressTime + 10, (rawEntity, queue, now) -> {
                    if (!(rawEntity instanceof ServerPlayer))
                        return;
                    ServerPlayer entity = (ServerPlayer) rawEntity;

                    InputCommand targetCommnad = InputCommand.M_DOWN;
                    boolean inputSucceed = CapabilityInputState.INPUT_STATE.maybeGet(entity)
                            .filter(input4 -> input4.getCommands().contains(targetCommnad)
                                    && input4.getCommands().contains(InputCommand.SNEAK)
                                    && input4.getCommands().contains(InputCommand.BACK)
                                    && !input4.getCommands().contains(InputCommand.FORWARD)
                                    && input4.getLastPressTime(targetCommnad) == pressTime)
                            .isPresent();
                    if (!inputSucceed)
                        return;

                    // summon
                    CapabilitySlashBlade.BLADESTATE.maybeGet(entity.getMainHandItem()).ifPresent((state) -> {

                        Level worldIn = entity.level();
                        Entity target = state.getTargetEntity(worldIn);

                        if (target == null || !target.isAlive() || target.isRemoved()) return;
                        if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get())
                            return;
                        state.setProudSoulCount(
                                state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
                        //烈风环影剑
                        AdvancementHelper.grantCriterion(entity, ADVANCEMENT_STORM_SWORDS);

                        int rank = CapabilityConcentrationRank.RANK_POINT.maybeGet(entity)
                                .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

                        int count = 6;

                        if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                            count = 8;
                        }

                        for (int i = 0; i < count; i++) {
                            EntityStormSwords ss = new EntityStormSwords(SBEntityTypes.StormSwords,
                                    worldIn);

                            ss.setPos(entity.position());
                            ss.setOwner(entity);
                            ss.setColor(state.getColorCode());
                            ss.setRoll(0);
                            ss.setDamage(powerLevel);
                            // force riding
                            ss.startRiding(target, true);
                            ss.setDelay(360 / count * i);
                            worldIn.addFreshEntity(ss);

                            entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                                    1.45F);
                        }
                    });
                });

                // BlisteringSwords command
                input.getScheduler().schedule("BlisteringSwords", pressTime + 10, (rawEntity, queue, now) -> {
                    if (!(rawEntity instanceof ServerPlayer))
                        return;
                    ServerPlayer entity = (ServerPlayer) rawEntity;

                    InputCommand targetCommnad = InputCommand.M_DOWN;
                    boolean inputSucceed = CapabilityInputState.INPUT_STATE.maybeGet(entity)
                            .filter(input1 -> input1.getCommands().contains(targetCommnad)
                                    && input1.getCommands().contains(InputCommand.SNEAK)
                                    && input1.getCommands().contains(InputCommand.FORWARD)
                                    && input1.getLastPressTime(InputCommand.BACK) + 20 < now
                                    && input1.getLastPressTime(targetCommnad) == pressTime)
                            .isPresent();
                    if (!inputSucceed)
                        return;

                    // summon
                    CapabilitySlashBlade.BLADESTATE.maybeGet(entity.getMainHandItem()).ifPresent((state) -> {

                        Level worldIn = entity.level();

                        if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get())
                            return;
                        state.setProudSoulCount(
                                state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
                        //急袭幻影剑
                        AdvancementHelper.grantCriterion(entity, ADVANCEMENT_BLISTERING_SWORDS);

                        int rank = CapabilityConcentrationRank.RANK_POINT.maybeGet(entity)
                                .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

                        int count = 6;

                        if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                            count = 8;
                        }

                        for (int i = 0; i < count; i++) {
                            EntityBlisteringSwords ss = new EntityBlisteringSwords(
                                    SBEntityTypes.BlisteringSwords, worldIn);

                            ss.setPos(entity.position());
                            ss.setOwner(entity);
                            ss.setColor(state.getColorCode());
                            ss.setRoll(0);
                            ss.setDamage(powerLevel);
                            // force riding
                            ss.startRiding(entity, true);

                            ss.setDelay(i);

                            worldIn.addFreshEntity(ss);

                            entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                                    1.45F);
                        }
                    });
                });

                // BlisteringSwords command
                input.getScheduler().schedule("HeavyRainSwords", pressTime + 10, (rawEntity, queue, now) -> {
                    if (!(rawEntity instanceof ServerPlayer))
                        return;
                    ServerPlayer entity = (ServerPlayer) rawEntity;

                    InputCommand targetCommnad = InputCommand.M_DOWN;
                    boolean inputSucceed = CapabilityInputState.INPUT_STATE.maybeGet(entity)
                            .filter(input2 -> input2.getCommands().contains(targetCommnad)
                                    && input2.getCommands().contains(InputCommand.SNEAK)
                                    && input2.getCommands().contains(InputCommand.FORWARD)
                                    && input2.getLastPressTime(InputCommand.BACK) + 30 > now
                                    && input2.getLastPressTime(targetCommnad) == pressTime)
                            .isPresent();
                    if (!inputSucceed)
                        return;

                    // summon
                    CapabilitySlashBlade.BLADESTATE.maybeGet(entity.getMainHandItem()).ifPresent((state) -> {

                        Level worldIn = entity.level();
                        Entity target = state.getTargetEntity(worldIn);
                        if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get())
                            return;
                        state.setProudSoulCount(
                                state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

                        //五月雨
                        AdvancementHelper.grantCriterion(entity, ADVANCEMENT_HEAVY_RAIN_SWORDS);

                        int rank = CapabilityConcentrationRank.RANK_POINT.maybeGet(entity)
                                .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

                        Vec3 basePos;

                        if (target != null) {
                            basePos = target.position();
                        } else {
                            Vec3 forwardDir = calculateViewVector(0, entity.getYRot());
                            basePos = entity.getPosition(0).add(forwardDir.scale(5));
                        }

                        float yOffset = 7;
                        basePos = basePos.add(0, yOffset, 0);

                        {// no random pos
                            EntityHeavyRainSwords ss = new EntityHeavyRainSwords(
                                    SBEntityTypes.HeavyRainSwords, worldIn);

                            ss.setOwner(entity);
                            ss.setColor(state.getColorCode());
                            ss.setRoll(0);
                            ss.setDamage(powerLevel);
                            // force riding
                            ss.startRiding(entity, true);

                            ss.setDelay(0);

                            ss.setPos(basePos);

                            ss.setXRot(-90);

                            worldIn.addFreshEntity(ss);
                        }

                        int count = 9 + Math.min(rank - 1, 0);
                        int multiplier = 2;
                        for (int i = 0; i < count; i++)
                            for (int l = 0; l < multiplier; l++) {
                                EntityHeavyRainSwords ss = new EntityHeavyRainSwords(
                                        SBEntityTypes.HeavyRainSwords, worldIn);

                                ss.setOwner(entity);
                                ss.setColor(state.getColorCode());
                                ss.setRoll(0);
                                ss.setDamage(powerLevel);
                                // force riding
                                ss.startRiding(entity, true);

                                ss.setDelay(i);

                                ss.setSpread(basePos);

                                ss.setXRot(-90);

                                worldIn.addFreshEntity(ss);

                                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                                        1.45F);
                            }
                    });
                });

            });

            CapabilitySlashBlade.BLADESTATE.maybeGet(blade).ifPresent((state) -> {

                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_COST.get())
                    return;
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_COST.get());
                //幻影剑
                AdvancementHelper.grantCriterion(sender, ADVANCEMENT_SUMMONEDSWORDS);

                Optional<Entity> foundTarget = findTarget(sender, state.getTargetEntity(sender.level()));

                Level worldIn = sender.level();
                Vec3 targetPos = foundTarget.map((e) -> new Vec3(e.getX(), e.getY() + e.getEyeHeight() * 0.5, e.getZ()))
                        .orElseGet(() -> {
                            Vec3 start = sender.getEyePosition(1.0f);
                            Vec3 end = start.add(sender.getLookAngle().scale(40));
                            HitResult result = worldIn.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE, sender));
                            return result.getLocation();
                        });

                int counter = StatHelper.increase(sender, SBStatTypes.SWORD_SUMMONED, 1);
                boolean sided = counter % 2 == 0;

                EntityAbstractSummonedSword ss = new EntityAbstractSummonedSword(
                        SBEntityTypes.SummonedSword, worldIn);

                Vec3 pos = sender.getEyePosition(1.0f)
                        .add(VectorHelper.getVectorForRotation(0.0f, sender.getViewYRot(0) + 90).scale(sided ? 1 : -1));
                ss.setPos(pos.x, pos.y, pos.z);
                ss.setDamage(powerLevel);
                Vec3 dir = targetPos.subtract(pos).normalize();
                ss.shoot(dir.x, dir.y, dir.z, 3.0f, 0.0f);
                // ss.setDamage(counter);
                ss.setOwner(sender);
                ss.setColor(state.getColorCode());
                ss.setRoll(sender.getRandom().nextFloat() * 360.0f);
                worldIn.addFreshEntity(ss);

                sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            });
        }
    }

    public Optional<Entity> findTarget(ServerPlayer sender, Entity lockedT) {
        Optional<Entity> foundTarget = Stream.of(Optional.ofNullable(lockedT),
                        RayTraceHelper
                                .rayTrace(sender.level(), sender, sender.getEyePosition(1.0f), sender.getLookAngle(),
                                        12, 12, (e) -> true)
                                .filter(r -> r.getType() == HitResult.Type.ENTITY).filter(r -> {
                                    EntityHitResult er = (EntityHitResult) r;
                                    Entity target = er.getEntity();

                                    boolean isMatch = true;
                                    if (target instanceof LivingEntity)
                                        isMatch = TargetSelector.lockon.test(sender, (LivingEntity) target);

                                    if (target instanceof IShootable)
                                        isMatch = ((IShootable) target).getShooter() != sender;

                                    return isMatch;
                                }).map(r -> ((EntityHitResult) r).getEntity()))
                .filter(Optional::isPresent).map(Optional::get).findFirst();
        return foundTarget;
    }

    Vec3 calculateViewVector(float x, float y) {
        float f = x * ((float) Math.PI / 180F);
        float f1 = -y * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
