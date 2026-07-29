package mods.flammpfeil.slashblade.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.client.renderer.entity.state.SummonedSwordRenderState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class SummonedSwordRenderer<T extends EntityAbstractSummonedSword> extends EntityRenderer<T, SummonedSwordRenderState> {

    @Nullable
    public Identifier getTextureLocation(T entity) {
        return entity.getTextureLoc();
    }

    public SummonedSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public SummonedSwordRenderState createRenderState() {
        return new SummonedSwordRenderState();
    }

    @Override
    public void extractRenderState(T entity, SummonedSwordRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        Entity hits = entity.getHitEntity();
        boolean hasHitEntity = hits != null;
        state.hasHitEntity = hasHitEntity;
        if (hasHitEntity) {
            state.hitsYRotO = hits.yRotO;
            state.hitsYRot = hits.getYRot();
        }
        state.yRotO = entity.yRotO;
        state.yRot = entity.getYRot();
        state.xRotO = entity.xRotO;
        state.xRot = entity.getXRot();
        state.color = entity.getColor();
        state.roll = entity.getRoll();
        state.offsetYaw = entity.getOffsetYaw();
        state.modelLoc = entity.getModelLoc();
        state.textureLoc = getTextureLocation(entity);
    }

    @Override
    public void submit(SummonedSwordRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {
            boolean hasHitEntity = state.hasHitEntity;

            if (hasHitEntity) {
                poseStack.mulPose(Axis.YN.rotationDegrees(Mth.rotLerp(state.sb$partialTick(), state.hitsYRotO, state.hitsYRot) - 90));
                poseStack.mulPose(Axis.YN.rotationDegrees(state.offsetYaw));
            } else {
                poseStack.mulPose(
                        Axis.YP.rotationDegrees(Mth.rotLerp(state.sb$partialTick(), state.yRotO, state.yRot) - 90.0F));
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(state.sb$partialTick(), state.xRotO, state.xRot)));

            poseStack.mulPose(Axis.XP.rotationDegrees(state.roll));

            float scale = 0.0075f;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            if (hasHitEntity) {
                poseStack.translate(0, 0, -100);
            }

            // poseStack.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
            WavefrontObject model = BladeModelManager.getInstance().getModel(state.modelLoc);
            BladeRenderState.setCol(state.color, false);
            BladeRenderState.renderOverridedLuminous(BladeItemRenderState.EMPTY, model, "ss", state.textureLoc,
                    poseStack, submitNodeCollector, state.lightCoords);
        }
    }
}