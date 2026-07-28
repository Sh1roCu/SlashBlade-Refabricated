package mods.flammpfeil.slashblade.client.renderer.entity;

import cn.sh1rocu.slashblade.api.extension.IEntityRepresentation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.client.renderer.entity.state.BladeStandEntityRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class BladeStandEntityRenderer extends ItemFrameRenderer<BladeStandEntity> {
    public BladeStandEntityRenderer(EntityRendererProvider.Context context) {
        super(context);

    }

    @Override
    public ItemFrameRenderState createRenderState() {
        return new BladeStandEntityRenderState();
    }

    @Override
    public void extractRenderState(BladeStandEntity entity, ItemFrameRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        if (entity.currentTypeStack.isEmpty()) {
            if (entity.currentType == null || entity.currentType == Items.AIR) {
                entity.currentTypeStack = new ItemStack(Items.ITEM_FRAME);
            } else {
                entity.currentTypeStack = new ItemStack(entity.currentType);
            }
            ((IEntityRepresentation) (Object) entity.currentTypeStack).sb$setEntityRepresentation(entity);
        }

        if (state instanceof BladeStandEntityRenderState bState) {
            bState.blockPos = entity.getPos();
            bState.position = entity.position();
            bState.xRot = entity.getXRot();
            bState.yRot = entity.getYRot();
            bState.rotation = entity.getRotation();
            bState.currentType = entity.currentType;
        }
    }

    @Override
    public void submit(ItemFrameRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state instanceof BladeStandEntityRenderState bState) {
            doRender(bState, poseStack, submitNodeCollector);
        }
    }

    public void doRender(BladeStandEntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {
            BlockPos blockpos = state.blockPos;
            Vec3 vec = Vec3.upFromBottomCenterOf(blockpos, 0.75).subtract(state.position);
            matrixStackIn.translate(vec.x, vec.y, vec.z);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(state.xRot));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));

            try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(matrixStackIn)) {
                int i = state.rotation;
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float) i * 360.0F / 8.0F));

                matrixStackIn.scale(2, 2, 2);
                Item type = state.currentType;
                if (type == SBItems.BLADESTAND_1) {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90f));
                } else if (type == SBItems.BLADESTAND_2) {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90f));
                } else if (type == SBItems.BLADESTAND_V) {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90f));
                } else if (type == SBItems.BLADESTAND_S) {
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90f));
                } else if (type == SBItems.BLADESTAND_1_W) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
                    matrixStackIn.translate(0, 0, -0.15f);
                } else if (type == SBItems.BLADESTAND_2_W) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180f));
                    matrixStackIn.translate(0, 0, -0.15f);
                }

                // stand render
                if (!state.frameModel.isEmpty()) {
                    matrixStackIn.pushPose();
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
                    matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                    matrixStackIn.translate(0, 0, 0.44);
                    state.frameModel.submitWithZOffset(matrixStackIn, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                    matrixStackIn.popPose();
                }

                if (state.currentType == SBItems.BLADESTAND_1_W || type == SBItems.BLADESTAND_2_W) {
                    matrixStackIn.translate(0, 0, -0.19f);
                } else if (state.currentType == SBItems.BLADESTAND_1) {
                }
                // blade render
                if (!state.item.isEmpty()) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180f));
                    state.item.submit(matrixStackIn, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                }

            }
        }
    }

}
