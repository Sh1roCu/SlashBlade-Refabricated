package mods.flammpfeil.slashblade.ability;

import cn.sh1rocu.slashblade.api.event.PlayerTickEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumSet;

public class KickJump {
    private static final class SingletonHolder {
        private static final KickJump instance = new KickJump();
    }

    public static KickJump getInstance() {
        return SingletonHolder.instance;
    }

    private KickJump() {
    }

    public void register() {
        InputCommandEvent.CALLBACK.register(this::onInputChange);
        PlayerTickEvent.START.register(this::onTick);
    }

    static final TargetingConditions tc = new TargetingConditions(false).ignoreLineOfSight()
            .ignoreInvisibilityTesting();

    public static final ResourceLocation ADVANCEMENT_KICK_JUMP = new ResourceLocation(SlashBlade.MODID,
            "abilities/kick_jump");

    public static final String KEY_KICKJUMP = "sb.kickjump";

    public void onInputChange(InputCommandEvent event) {

        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();
        Level worldIn = sender.level();

        if (sender.onGround())
            return;
        if (old.contains(InputCommand.JUMP))
            return;
        if (!current.contains(InputCommand.JUMP))
            return;

        if (0 != ((EntityExtension) sender).sb$getPersistentData().getInt(KEY_KICKJUMP))
            return;

        Iterable<VoxelShape> list = worldIn.getBlockCollisions(sender, sender.getBoundingBox().inflate(0.5, 0, 1));
        if (!list.iterator().hasNext())
            return;

        // 保存当前疾跑状态
        boolean wasSprinting = sender.isSprinting();

        // execute
        Untouchable.setUntouchable(sender, Untouchable.JUMP_TICKS);

        // set cooldown
        ((EntityExtension) sender).sb$getPersistentData().putInt(KEY_KICKJUMP, 2);

        Vec3 delta = sender.getDeltaMovement();
        Vec3 motion = new Vec3(delta.x, +0.8, delta.z);

        sender.move(MoverType.SELF, motion);

        //疾跑时保持水平动量，非疾跑时保持原缩放
        Vec3 adjustedMotion = wasSprinting
                ? new Vec3(motion.x, motion.y * 0.75f, motion.z) // 只缩放垂直分量
                : motion.scale(0.75f);                           // 整体缩放

        sender.connection.send(new ClientboundSetEntityMotionPacket(sender.getId(), adjustedMotion));

        //强制恢复疾跑状态
        if (wasSprinting) {
            sender.setSprinting(true);
        }

        AdvancementHelper.grantCriterion(sender, ADVANCEMENT_KICK_JUMP);
        sender.playNotifySound(SoundEvents.PLAYER_SMALL_FALL, SoundSource.PLAYERS, 0.5f, 1.2f);

        CapabilitySlashBlade.BLADESTATE.maybeGet(sender.getMainHandItem()).ifPresent(s -> {
            s.updateComboSeq(sender, ComboStateRegistry.getId(ComboStateRegistry.NONE));
        });

        if (worldIn instanceof ServerLevel) {
            ((ServerLevel) worldIn).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()), sender.getX(),
                    sender.getY(), sender.getZ(), 20, 0.0D, 0.0D, 0.0D, (double) 0.15F);
        }
    }

    public void onTick(PlayerTickEvent.Pre event) {
        LivingEntity player = event.getEntity();
        // cooldown
        if (player.onGround() && 0 < ((EntityExtension) player).sb$getPersistentData().getInt(KEY_KICKJUMP)) {

            int count = ((EntityExtension) player).sb$getPersistentData().getInt(KEY_KICKJUMP);
            count--;

            if (count <= 0) {
                ((EntityExtension) player).sb$getPersistentData().remove(KEY_KICKJUMP);
            } else {
                ((EntityExtension) player).sb$getPersistentData().putInt(KEY_KICKJUMP, count);
            }
        }
    }
}