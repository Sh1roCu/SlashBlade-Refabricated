package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.event.LivingEntityRenderEvents;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class UserPoseOverrider {

    public static boolean UsePoseOverrider = false;

    private static final class SingletonHolder {
        private static final UserPoseOverrider instance = new UserPoseOverrider();
    }

    public static UserPoseOverrider getInstance() {
        return SingletonHolder.instance;
    }

    private UserPoseOverrider() {
    }

    public void register() {
        LivingEntityRenderEvents.PRE.register((LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light) -> {
            this.onRenderPlayerEventPre(entity, renderer, partialRenderTick, matrixStack, buffers, light);
            return false;
        });
        UsePoseOverrider = true;
    }

    private static final String TAG_ROT = "sb_yrot";
    private static final String TAG_ROT_PREV = "sb_yrot_prev";

    public void onRenderPlayerEventPre(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialTicks, PoseStack matrixStackIn, MultiBufferSource buffers, int light) {
        ItemStack stack = entity.getMainHandItem();

        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        float rot = ((EntityExtension) entity).sb$getPersistentData().getFloat(TAG_ROT);
        float rotPrev = ((EntityExtension) entity).sb$getPersistentData().getFloat(TAG_ROT_PREV);

        float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - f));
        anotherPoseRotP(matrixStackIn, entity, partialTicks);

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, rot, rotPrev)));

        anotherPoseRotN(matrixStackIn, entity, partialTicks);
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(180.0F - f));
    }

    public static void anotherPoseRotP(PoseStack matrixStackIn, LivingEntity entityLiving, float partialTicks) {
        final boolean isPositive = true;
        final float np = isPositive ? 1 : -1;

        float f = entityLiving.getSwimAmount(partialTicks);
        if (entityLiving.isFallFlying()) {
            float f1 = (float) entityLiving.getFallFlyingTicks() + partialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entityLiving.isAutoSpinAttack()) {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f2 * (-90.0F - entityLiving.getXRot())));
            }

            Vec3 vector3d = entityLiving.getViewVector(partialTicks);
            Vec3 vector3d1 = entityLiving.getDeltaMovement();
            double d0 = vector3d1.horizontalDistanceSqr();
            double d1 = vector3d.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStackIn.mulPose(Axis.YP.rotation((float) (np * Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f4));
            if (entityLiving.isVisuallySwimming()) {
                matrixStackIn.translate(0.0D, np * -1.0D, (double) np * 0.3F);
            }
        }
    }

    public static void anotherPoseRotN(PoseStack matrixStackIn, LivingEntity entityLiving, float partialTicks) {
        final boolean isPositive = false;
        final float np = isPositive ? 1 : -1;

        float f = entityLiving.getSwimAmount(partialTicks);
        if (entityLiving.isFallFlying()) {
            Vec3 vector3d = entityLiving.getViewVector(partialTicks);
            Vec3 vector3d1 = entityLiving.getDeltaMovement();
            double d0 = vector3d1.horizontalDistanceSqr();
            double d1 = vector3d.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStackIn.mulPose(Axis.YP.rotation((float) (np * Math.signum(d3) * Math.acos(d2))));
            }

            float f1 = (float) entityLiving.getFallFlyingTicks() + partialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entityLiving.isAutoSpinAttack()) {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f2 * (-90.0F - entityLiving.getXRot())));
            }
        } else if (f > 0.0F) {
            if (entityLiving.isVisuallySwimming()) {
                matrixStackIn.translate(0.0D, np * -1.0D, (double) np * 0.3F);
            }

            float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f4));
        }
    }

    public static void setRot(Entity target, float rotYaw, boolean isOffset) {
        CompoundTag tag = ((EntityExtension) target).sb$getPersistentData();

        float prevRot = tag.getFloat(TAG_ROT);
        tag.putFloat(TAG_ROT_PREV, prevRot);

        if (isOffset)
            rotYaw += prevRot;

        tag.putFloat(TAG_ROT, rotYaw);
    }

    public static void resetRot(Entity target) {
        CompoundTag tag = ((EntityExtension) target).sb$getPersistentData();
        tag.putFloat(TAG_ROT_PREV, 0);
        tag.putFloat(TAG_ROT, 0);
    }

    public static void invertRot(PoseStack matrixStack, Entity entity, float partialTicks) {
        float rot = ((EntityExtension) entity).sb$getPersistentData().getFloat(TAG_ROT);
        float rotPrev = ((EntityExtension) entity).sb$getPersistentData().getFloat(TAG_ROT_PREV);
        matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, rot, rotPrev)));
    }
}
