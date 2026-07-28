package mods.flammpfeil.slashblade.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.entity.state.JudgementCutRenderState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class JudgementCutRenderer<T extends EntityJudgementCut> extends EntityRenderer<T, JudgementCutRenderState> {

    static private final Identifier modelLocation = Identifier.fromNamespaceAndPath(SlashBlade.MODID,
            "model/util/slashdim.obj");
    static private final Identifier textureLocation = Identifier.fromNamespaceAndPath(SlashBlade.MODID,
            "model/util/slashdim.png");

    public JudgementCutRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public JudgementCutRenderState createRenderState() {
        return new JudgementCutRenderState();
    }

    @Override
    public void extractRenderState(T entity, JudgementCutRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.lifetime = entity.getLifetime();
        state.tickCount = entity.tickCount;
        state.yRotO = entity.yRotO;
        state.yRot = entity.getYRot();
        state.xRotO = entity.xRotO;
        state.xRot = entity.getXRot();
        state.color = entity.getColor();
        state.seed = entity.getSeed();
    }

    @Override
    public void submit(JudgementCutRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {

            poseStack
                    .mulPose(Axis.YP.rotationDegrees(Mth.lerp(state.sb$partialTick(), state.yRotO, state.yRot) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(state.sb$partialTick(), state.xRotO, state.xRot)));

            WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

            int lifetime = state.lifetime;

            double deathTime = lifetime;
            // double baseAlpha = Math.sin(Math.PI * 0.5 * (Math.min(deathTime, Math.max(0,
            // (lifetime - (entity.ticksExisted) - partialTicks))) / deathTime));
            double baseAlpha = (Math.clamp(lifetime - (state.tickCount) - state.sb$partialTick(), 0, deathTime)
                    / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0) + 1.0;

            int seed = state.seed;

            poseStack.mulPose(Axis.YP.rotationDegrees(seed));

            float scale = 0.01f;
            poseStack.scale(scale, scale, scale);

            int color = state.color & 0xFFFFFF;
            Color col = new Color(color);
            float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
            int baseColor = Color.HSBtoRGB(0.5f + hsb[0], hsb[1], 0.2f/* hsb[2] */) & 0xFFFFFF;

            try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(poseStack)) {
                for (int l = 0; l < 5; l++) {
                    poseStack.scale(0.95f, 0.95f, 0.95f);

                    BladeRenderState.setCol(baseColor | ((0xFF & (int) (0x66 * baseAlpha)) << 24));
                    BladeRenderState.renderOverridedReverseLuminous(BladeItemRenderState.EMPTY, model, "base",
                            textureLocation, poseStack, submitNodeCollector, state.lightCoords);
                }
            }

            int loop = 3;
            for (int l = 0; l < loop; l++) {
                try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(poseStack)) {
                    float cycleTicks = 15;
                    float wave = (state.tickCount + (cycleTicks / (float) loop * l) + state.sb$partialTick()) % cycleTicks;
                    float waveScale = 1.0f + 0.03f * wave;
                    poseStack.scale(waveScale, waveScale, waveScale);

                    BladeRenderState
                            .setCol(baseColor | ((int) (0x88 * ((cycleTicks - wave) / cycleTicks) * baseAlpha) << 24));
                    BladeRenderState.renderOverridedReverseLuminous(BladeItemRenderState.EMPTY, model, "base",
                            textureLocation, poseStack, submitNodeCollector, state.lightCoords);
                }
            }

            int windCount = 5;
            for (int l = 0; l < windCount; l++) {
                try (MSAutoCloser msacB = MSAutoCloser.pushMatrix(poseStack)) {

                    poseStack.mulPose(Axis.XP.rotationDegrees((360.0f / windCount) * l));
                    poseStack.mulPose(Axis.YP.rotationDegrees(30.0f));

                    double rotWind = 360.0 / 20.0;

                    double offsetBase = 7;

                    double offset = l * offsetBase;

                    double motionLen = offsetBase * (windCount - 1);

                    double ticks = state.tickCount + state.sb$partialTick() + seed;
                    double offsetTicks = ticks + offset;
                    double progress = (offsetTicks % motionLen) / motionLen;

                    double rad = (Math.PI) * 2.0;
                    rad *= progress;

                    float windScale = (float) (0.4 + progress);
                    poseStack.scale(windScale, windScale, windScale);

                    poseStack.mulPose(Axis.ZP.rotationDegrees((float) (rotWind * offsetTicks)));

                    Color cc = new Color(col.getRed(), col.getGreen(), col.getBlue(),
                            0xff & (int) (Math.min(0, 0xFF * Math.sin(rad) * baseAlpha)));
                    BladeRenderState.setCol(cc);
                    BladeRenderState.renderOverridedColorWrite(BladeItemRenderState.EMPTY, model, "wind",
                            textureLocation, poseStack, submitNodeCollector, BladeRenderState.MAX_LIGHT);
                }
            }
        }
    }
}