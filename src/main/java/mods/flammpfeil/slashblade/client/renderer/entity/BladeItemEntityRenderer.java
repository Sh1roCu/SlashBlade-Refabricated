package mods.flammpfeil.slashblade.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.renderer.entity.state.BladeItemEntityRenderState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.BladeBaseRenderer;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.item.SwordType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;

@Environment(EnvType.CLIENT)
public class BladeItemEntityRenderer extends ItemEntityRenderer {
    public BladeItemEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new BladeItemEntityRenderState();
    }

    @Override
    public void extractRenderState(ItemEntity entity, ItemEntityRenderState renderState, float partialTicks) {
        super.extractRenderState(entity, renderState, partialTicks);
        if (entity instanceof BladeItemEntity bladeItem && renderState instanceof BladeItemEntityRenderState bSate) {
            bSate.itemRenderState = BladeBaseRenderer.createBladeItemRenderState(entity.getItem());
            bSate.modelLocation = CapabilitySlashBlade.getBladeState(entity.getItem())
                    .map((state) -> state.getModel().orElseGet(bladeItem::getModel)).orElseGet(bladeItem::getModel);
            bSate.textureLocation = CapabilitySlashBlade.getBladeState(entity.getItem())
                    .map((state) -> state.getTexture().orElseGet(bladeItem::getTexture))
                    .orElseGet(bladeItem::getTexture);
            bSate.isInWater = entity.isInWater();
            bSate.onGround = entity.onGround();
            bSate.tickCount = entity.tickCount;
            bSate.entityYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        }
    }

    @Override
    public void submit(ItemEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.shadowRadius = 0;
        if (!state.item.isEmpty() && state instanceof BladeItemEntityRenderState bState) {
            renderBlade(bState, poseStack, submitNodeCollector);
        } else {
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    private void renderBlade(BladeItemEntityRenderState state, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector) {
        if (state.itemRenderState != null) {
            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.entityYaw));

                EnumSet<SwordType> types = state.itemRenderState.swordTypes;
                Identifier modelLocation = state.modelLocation;
                Identifier textureLocation = state.textureLocation;

                WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

                float scale = 0.00625f;

                try (MSAutoCloser msac2 = MSAutoCloser.pushMatrix(matrixStackIn)) {

                    float heightOffset;
                    float xOffset = 0;
                    String renderTarget;
                    if (types.contains(SwordType.EDGEFRAGMENT)) {
                        heightOffset = 225;
                        xOffset = 200;
                        renderTarget = "blade_fragment";
                    } else if (types.contains(SwordType.BROKEN)) {
                        heightOffset = 100;
                        xOffset = 30;
                        renderTarget = "blade_damaged";
                    } else {
                        heightOffset = 225;
                        xOffset = 120;
                        renderTarget = "blade";
                    }

                    if (state.isInWater) {

                        matrixStackIn.translate(0, 0.025f, 0);
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.bobOffset));

                        matrixStackIn.scale(scale, scale, scale);

                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));

                    } else if (!state.onGround) {
                        matrixStackIn.scale(scale, scale, scale);

                        float speed = -81f;
                        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(speed * (state.tickCount + state.sb$partialTick())));
                        matrixStackIn.translate(xOffset, 0, 0);
                    } else {
                        matrixStackIn.scale(scale, scale, scale);

                        matrixStackIn
                                .mulPose(Axis.ZP.rotationDegrees(60 + (float) Math.toDegrees(state.bobOffset / 6.0)));
                        matrixStackIn.translate(heightOffset, 0, 0);
                    }

                    BladeRenderState.renderOverrided(state.itemRenderState, model, renderTarget, textureLocation, matrixStackIn,
                            submitNodeCollector, state.lightCoords);
                    BladeRenderState.renderOverridedLuminous(state.itemRenderState, model, renderTarget + "_luminous",
                            textureLocation, matrixStackIn, submitNodeCollector, state.lightCoords);
                }

                if (state.isInWater || state.onGround && !types.contains(SwordType.NOSCABBARD)) {

                    try (MSAutoCloser msac2 = MSAutoCloser.pushMatrix(matrixStackIn)) {

                        matrixStackIn.translate(0, 0.025f, 0);

                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(state.bobOffset));

                        if (!state.isInWater) {
                            matrixStackIn.translate(0.75, 0, -0.4);
                        }

                        matrixStackIn.scale(scale, scale, scale);

                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));

                        String renderTarget = "sheath";

                        BladeRenderState.renderOverrided(state.itemRenderState, model, renderTarget, textureLocation, matrixStackIn,
                                submitNodeCollector, state.lightCoords);
                        BladeRenderState.renderOverridedLuminous(state.itemRenderState, model, renderTarget + "_luminous",
                                textureLocation, matrixStackIn, submitNodeCollector, state.lightCoords);
                    }
                }
            }
        }
    }
}
