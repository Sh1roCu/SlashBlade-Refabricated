package mods.flammpfeil.slashblade.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.entity.state.DriveRenderState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class DriveRenderer<T extends EntityDrive> extends EntityRenderer<T, DriveRenderState> {

    private static final Identifier TEXTURE = SlashBlade.prefix("model/util/ss.png");
    private static final Identifier MODEL = SlashBlade.prefix("model/util/drive.obj");

    public DriveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public DriveRenderState createRenderState() {
        return new DriveRenderState();
    }

    @Override
    public void extractRenderState(T entity, DriveRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.lifetime = entity.getLifetime();
        state.tickCount = entity.tickCount;
        state.yRotO = entity.yRotO;
        state.yRot = entity.getYRot();
        state.xRotO = entity.xRotO;
        state.xRot = entity.getXRot();
        state.rotationRoll = entity.getRotationRoll();
        state.color = entity.getColor();
    }

    @Override
    public void submit(DriveRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {
            float lifetime = state.lifetime;
            double deathTime = lifetime;
            double baseAlpha = (Math.clamp(lifetime - (state.tickCount), 0, deathTime)
                    / deathTime);
            baseAlpha = Math.max(0, -Math.pow(baseAlpha - 1, 4.0) + 0.75);

            poseStack.mulPose(
                    Axis.YP.rotationDegrees(Mth.rotLerp(state.sb$partialTick(), state.yRotO, state.yRot - 90.0F)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(state.sb$partialTick(), state.xRotO, state.xRot)));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.rotationRoll));

            float scale = 0.015f;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            int color = state.color & 0xFFFFFF;
            int alpha = ((0xFF & (int) (0xFF * baseAlpha)) << 24);
            WavefrontObject model = BladeModelManager.getInstance().getModel(MODEL);

            BladeRenderState.setCol(color | alpha);
            BladeRenderState.renderOverridedLuminous(BladeItemRenderState.EMPTY, model, "base", TEXTURE, poseStack, submitNodeCollector,
                    state.lightCoords);
        }
    }
}
