package mods.flammpfeil.slashblade.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank.ConcentrationRanks;
import mods.flammpfeil.slashblade.client.renderer.entity.state.SlashEffectRenderState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SlashEffectRenderer<T extends EntitySlashEffect> extends EntityRenderer<T, SlashEffectRenderState> {

    static private final Identifier modelLocation = Identifier.fromNamespaceAndPath(SlashBlade.MODID,
            "model/util/slash.obj");
    static private final Identifier textureLocation = Identifier.fromNamespaceAndPath(SlashBlade.MODID,
            "model/util/slash.png");

    public SlashEffectRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public SlashEffectRenderState createRenderState() {
        return new SlashEffectRenderState();
    }

    @Override
    public void extractRenderState(T entity, SlashEffectRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, state.sb$partialTick());
        state.lifetime = entity.getLifetime();
        state.tickCount = entity.tickCount;
        state.yRotO = entity.yRotO;
        state.yRot = entity.getYRot();
        state.xRotO = entity.xRotO;
        state.xRot = entity.getXRot();
        state.color = entity.getColor();
        state.rotationRoll = entity.getRotationRoll();
        state.rotationOffset = entity.getRotationOffset();
        state.baseSize = entity.getBaseSize();
        state.rankCode = entity.getRankCode();
    }

    @Override
    public void submit(SlashEffectRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(poseStack)) {

            poseStack
                    .mulPose(Axis.YP.rotationDegrees(-Mth.lerp(state.sb$partialTick(), state.yRotO, state.yRot) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(state.sb$partialTick(), state.xRotO, state.xRot)));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.rotationRoll));

            WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);

            int lifetime = state.lifetime;

            float progress = Math.min(lifetime, (state.tickCount + state.sb$partialTick())) / lifetime;

            double deathTime = lifetime;
            // double baseAlpha = Math.sin(Math.PI * 0.5 * (Math.min(deathTime, Math.max(0,
            // (lifetime - (entity.ticksExisted) - state.sb$partialTick()))) / deathTime));
            double baseAlpha = (Math.clamp(lifetime - (state.tickCount) - state.sb$partialTick(), 0, deathTime)
                    / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0) + 1.0;

            // baseAlpha = Math.sin(-Math.PI + Math.PI * 2 * progress) * 0.5f + 0.5f;
            // baseAlpha = Math.sin(Math.PI * progress);

            // time
            poseStack.mulPose(Axis.YP.rotationDegrees(state.rotationOffset - 135.0F * progress));

            poseStack.scale(1, 0.25f, 1);

            float baseScale = 1.2f;
            poseStack.scale(baseScale, baseScale, baseScale);

            float yscale = 0.03f;
            float scale = state.baseSize * Mth.lerp(progress, 0.03f, 0.035f);

            int color = state.color & 0xFFFFFF;

            ConcentrationRanks rank = state.rankCode;

            // rank color overwrite
            if (rank.level < ConcentrationRanks.C.level) {
                color = 0x555555;
            }

            Identifier rl = textureLocation;

            // baseAlpha = 1.0f;
            int alpha = ((0xFF & (int) (0xFF * baseAlpha)) << 24);

            // black alpha insidee
            if (ConcentrationRanks.S.level <= rank.level)
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(poseStack)) {
                    float windscale = state.baseSize * Mth.lerp(progress, 0.035f, 0.03f);
                    poseStack.scale(windscale, yscale, windscale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.8f + progress * 0.3f);
                    BladeRenderState.setCol(0x222222 | alpha);
                    BladeRenderState.renderOverridedColorWrite(BladeItemRenderState.EMPTY, model, "base", rl, poseStack,
                            submitNodeCollector, state.lightCoords);
                }

            // color alpha base
            if (ConcentrationRanks.D.level <= rank.level)
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(poseStack)) {
                    poseStack.scale(scale, yscale, scale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                    BladeRenderState.setCol(color | alpha);
                    BladeRenderState.renderOverridedColorWrite(BladeItemRenderState.EMPTY, model, "base", rl, poseStack,
                            submitNodeCollector, state.lightCoords);
                }

            // white add outside
            if (ConcentrationRanks.B.level <= rank.level)
                try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(poseStack)) {
                    float windscale = state.baseSize * Mth.lerp(progress, 0.03f, 0.0375f);
                    poseStack.scale(windscale, yscale, windscale);
                    Face.setAlphaOverride(Face.alphaOverrideYZZ);
                    Face.setUvOperator(1, 1, 0, -0.5f + progress * -0.2f);
                    BladeRenderState.setCol(0x404040 | alpha);
                    BladeRenderState.renderOverridedLuminous(BladeItemRenderState.EMPTY, model, "base", rl, poseStack,
                            submitNodeCollector, state.lightCoords);
                }

            // color add base
            try (MSAutoCloser msacb = MSAutoCloser.pushMatrix(poseStack)) {
                poseStack.scale(scale, yscale, scale);
                Face.setAlphaOverride(Face.alphaOverrideYZZ);
                Face.setUvOperator(1, 1, 0, -0.35f + progress * -0.15f);
                BladeRenderState.setCol(color | alpha);
                BladeRenderState.renderOverridedLuminous(BladeItemRenderState.EMPTY, model, "base", rl, poseStack, submitNodeCollector,
                        state.lightCoords);
            }
        }
    }
}