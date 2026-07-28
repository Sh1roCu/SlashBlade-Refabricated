package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import cn.sh1rocu.slashblade.api.event.RenderLivingEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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
        RenderLivingEvent.PRE.register(this::onRenderPlayerEventPre);
        UsePoseOverrider = true;
    }

    public static final String TAG_ROT = "sb_yrot";
    public static final String TAG_ROT_PREV = "sb_yrot_prev";

    public <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> void onRenderPlayerEventPre(
            RenderLivingEvent.Pre<T, S, M> event) {
        var renderState = event.getRenderState();
        if (!(renderState instanceof HumanoidRenderState humanoidRenderState)) {
            return;
        }

        ItemStack stack = humanoidRenderState.getMainHandItemStack();

        if (stack.isEmpty()) {
            return;
        }
        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        float rot = humanoidRenderState.getDataOrDefault(RenderStateKeys.PERSISTENT_DATA_YROT, 0F);
        float rotPrev = humanoidRenderState.getDataOrDefault(RenderStateKeys.PERSISTENT_DATA_PREV_YROT, 0F);
        // TODO: check
        // float f = Mth.rotLerp(event.getPartialTick(), entity.yBodyRotO, entity.yBodyRot);
        float f = humanoidRenderState.bodyRot;
        var matrixStackIn = event.getPoseStack();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - f));
        anotherPoseRotP(matrixStackIn, humanoidRenderState, event.getPartialTick());

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(event.getPartialTick(), rot, rotPrev)));
        anotherPoseRotN(matrixStackIn, humanoidRenderState, event.getPartialTick());
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(180.0F - f));
    }

    public static void anotherPoseRotP(PoseStack matrixStackIn, HumanoidRenderState renderState, float partialTicks) {
        final float np = 1;

        float f = renderState.swimAmount;
        var extraData = renderState.getDataOrDefault(RenderStateKeys.EXTRA_ENTITY_RENDER_DATA,
                new RenderStateKeys.ExtraEntityRenderData(0, Vec3.ZERO, Vec3.ZERO));
        if (renderState.isFallFlying) {
            float f1 = (float) extraData.fallFlyingTicks + partialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!renderState.isAutoSpinAttack) {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f2 * (-90.0F - renderState.xRot)));
            }

            Vec3 vector3d = extraData.viewVector;
            Vec3 vector3d1 = extraData.deltaMovement;
            double d0 = vector3d1.horizontalDistanceSqr();
            double d1 = vector3d.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStackIn.mulPose(Axis.YP.rotation((float) (np * Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            float f3 = renderState.isInWater ? -90.0F - renderState.xRot : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f4));
            if (renderState.isVisuallySwimming) {
                matrixStackIn.translate(0.0D, np * -1.0D, (double) np * 0.3F);
            }
        }
    }

    public static void anotherPoseRotN(PoseStack matrixStackIn, HumanoidRenderState renderState, float partialTicks) {
        final float np = -1;

        float f = renderState.swimAmount;
        var extraData = renderState.getDataOrDefault(RenderStateKeys.EXTRA_ENTITY_RENDER_DATA,
                new RenderStateKeys.ExtraEntityRenderData(0, Vec3.ZERO, Vec3.ZERO));
        if (renderState.isFallFlying) {
            Vec3 vector3d = extraData.viewVector;
            Vec3 vector3d1 = extraData.deltaMovement;
            double d0 = vector3d1.horizontalDistanceSqr();
            double d1 = vector3d.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStackIn.mulPose(Axis.YP.rotation((float) (np * Math.signum(d3) * Math.acos(d2))));
            }

            float f1 = (float) extraData.fallFlyingTicks + partialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!renderState.isAutoSpinAttack) {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f2 * (-90.0F - renderState.xRot)));
            }
        } else if (f > 0.0F) {
            if (renderState.isVisuallySwimming) {
                matrixStackIn.translate(0.0D, np * -1.0D, (double) np * 0.3F);
            }

            float f3 = renderState.isInWater ? -90.0F - renderState.xRot : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(np * f4));
        }
    }

    public static void setRot(Entity target, float rotYaw, boolean isOffset) {
        CompoundTag tag = ((EntityExtension) target).sb$getPersistentData();

        float prevRot = tag.getFloatOr(TAG_ROT, 0);
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

    public static void invertRot(PoseStack matrixStack, LivingEntityRenderState renderState, float partialTicks) {
        float rot = renderState.getDataOrDefault(RenderStateKeys.PERSISTENT_DATA_YROT, 0F);
        float rotPrev = renderState.getDataOrDefault(RenderStateKeys.PERSISTENT_DATA_PREV_YROT, 0F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, rot, rotPrev)));
    }
}
