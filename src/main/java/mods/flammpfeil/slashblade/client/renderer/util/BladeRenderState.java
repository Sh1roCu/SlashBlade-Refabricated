package mods.flammpfeil.slashblade.client.renderer.util;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderPipelines.*;
import static net.minecraft.client.renderer.rendertype.TextureTransform.ENTITY_GLINT_TEXTURING;
import static net.minecraft.client.renderer.rendertype.TextureTransform.GLINT_TEXTURING;

public class BladeRenderState {

    private static final Color defaultColor = Color.white;
    private static Color col = defaultColor;

    public static void setCol(int rgba) {
        setCol(rgba, true);
    }

    public static void setCol(int rgb, boolean hasAlpha) {
        setCol(new Color(rgb, hasAlpha));
    }

    public static void setCol(Color value) {
        col = value;
    }

    public static final int MAX_LIGHT = 15728864;

    public static void resetCol() {
        col = defaultColor;
    }

    public static void renderOverrided(BladeItemRenderState state, WavefrontObject model, String target, Identifier texture,
                                       PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {

        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector,
                packedLightIn, BladeRenderState::getSlashBladeBlend, true);
    }

    public static void renderOverridedColorWrite(BladeItemRenderState state, WavefrontObject model, String target,
                                                 Identifier texture, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {
        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector, packedLightIn,
                BladeRenderState::getSlashBladeBlendColorWrite, true);
    }

    public static void renderChargeEffect(BladeItemRenderState state, float f, WavefrontObject model, String target,
                                          Identifier texture, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {
        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector, packedLightIn,
                (loc) -> BladeRenderState.getChargeEffect(loc, f * 0.1F % 1.0F, f * 0.01F % 1.0F), false);
    }

    public static void renderOverridedLuminous(BladeItemRenderState state, WavefrontObject model, String target,
                                               Identifier texture, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {
        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector, packedLightIn,
                BladeRenderState::getSlashBladeBlendLuminous, false);
    }

    public static void renderOverridedLuminousDepthWrite(BladeItemRenderState state, WavefrontObject model, String target,
                                                         Identifier texture, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {
        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector, packedLightIn,
                BladeRenderState::getSlashBladeBlendLuminousDepthWrite, false);
    }

    public static void renderOverridedReverseLuminous(BladeItemRenderState state, WavefrontObject model, String target,
                                                      Identifier texture, PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn) {
        renderOverrided(state, model, target, texture, matrixStackIn, submitNodeCollector, packedLightIn,
                BladeRenderState::getSlashBladeBlendReverseLuminous, false);
    }

    public static void renderOverrided(BladeItemRenderState state, WavefrontObject model, String target, Identifier texture,
                                       PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int packedLightIn,
                                       Function<Identifier, RenderType> getRenderType, boolean enableEffect) {
        RenderOverrideEvent event = RenderOverrideEvent.onRenderOverride(state, model, target, texture, matrixStackIn,
                submitNodeCollector, packedLightIn, getRenderType, enableEffect);

        if (event.isCanceled())
            return;

        Identifier loc = event.getTexture();

        RenderType rt = event.getGetRenderType().apply(loc);// getSlashBladeBlendLuminous(event.getTexture());

        int color = ARGB.color(
                col.getAlpha(),
                col.getRed(),
                col.getGreen(),
                col.getBlue()
        );

        submitNodeCollector.submitCustomGeometry(matrixStackIn, rt, (pose, vb) -> {
            matrixStackIn.pushPose();
            matrixStackIn.last().set(pose);
            event.getModel().tessellateOnly(vb, matrixStackIn, event.getPackedLightIn(), color, event.getTarget());
            matrixStackIn.popPose();
        });

        if (state.hasFoil && event.isEnableEffect()) {
            RenderType type = target.startsWith("item_") ? BladeRenderState.SLASHBLADE_ITEM_GLINT : BladeRenderState.SLASHBLADE_GLINT;
            submitNodeCollector.submitCustomGeometry(matrixStackIn, type, (pose, vb) -> {
                matrixStackIn.pushPose();
                matrixStackIn.last().set(pose);
                event.getModel().tessellateOnly(vb, matrixStackIn, event.getPackedLightIn(), color, event.getTarget());
                matrixStackIn.popPose();
            });
        }

        Face.resetAlphaOverride();
        Face.resetUvOperator();

        resetCol();
    }

    private static final Map<Identifier, RenderType> slashBladeBlendCache = new HashMap<>();
    private static final Map<Identifier, RenderType> slashBladeBlendColorWriteCache = new HashMap<>();
    private static final Map<Identifier, RenderType> slashBladeBlendLuminousCache = new HashMap<>();
    private static final Map<ChargeEffectKey, RenderType> chargeEffectCache = new HashMap<>();
    private static final Map<Identifier, RenderType> luminousDepthWriteCache = new HashMap<>();
    private static final Map<Identifier, RenderType> reverseLuminousCache = new HashMap<>();

    public static RenderType getSlashBladeBlend(Identifier texture) {
        return slashBladeBlendCache.computeIfAbsent(texture, t -> {
            RenderPipeline pipeline = RenderPipeline.builder(ITEM_SNIPPET)
                    .withLocation("pipeline/item_translucent")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", t)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_blend_" + t, state);
        });
    }

    public static final RenderType SLASHBLADE_GLINT = BladeRenderState.getSlashBladeGlint();

    private static RenderType getSlashBladeGlint() {
        RenderPipeline pipeline = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET, FOG_SNIPPET, GLOBALS_SNIPPET)
                .withLocation("pipeline/glint")
                .withVertexShader("core/glint")
                .withFragmentShader("core/glint")
                .withSampler("Sampler0")
                .withCull(false)
                .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES)
                .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
                .build();

        RenderSetup state = RenderSetup.builder(pipeline)
                .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .setTextureTransform(ENTITY_GLINT_TEXTURING)
                .useOverlay()
                .bufferSize(256)
                .createRenderSetup();

        return RenderType.create("slashblade_glint", state);
    }

    public static final RenderType SLASHBLADE_ITEM_GLINT = BladeRenderState.getSlashBladeItemGlint();

    private static RenderType getSlashBladeItemGlint() {
        RenderPipeline pipeline = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET, FOG_SNIPPET, GLOBALS_SNIPPET)
                .withLocation("pipeline/glint")
                .withVertexShader("core/glint")
                .withFragmentShader("core/glint")
                .withSampler("Sampler0")
                .withCull(false)
                .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES)
                .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
                .build();

        RenderSetup state = RenderSetup.builder(pipeline)
                .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .setTextureTransform(GLINT_TEXTURING)
                .useOverlay()
                .bufferSize(256)
                .createRenderSetup();

        return RenderType.create("slashblade_glint", state);
    }

    public static RenderType getSlashBladeBlendColorWrite(Identifier texture) {
        return slashBladeBlendColorWriteCache.computeIfAbsent(texture, t -> {
            RenderPipeline pipeline = RenderPipeline.builder(ENTITY_EMISSIVE_SNIPPET)
                    .withLocation("pipeline/entity_translucent_emissive")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withColorTargetState(new ColorTargetState(LIGHTNING_ADDITIVE_TRANSPARENCY))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", t)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_blend_write_color_" + t, state);
        });
    }

    protected static final BlendFunction LIGHTNING_ADDITIVE_TRANSPARENCY = new BlendFunction(
            SourceFactor.SRC_ALPHA, DestFactor.ONE,
            SourceFactor.ONE, DestFactor.ZERO);

    public static RenderType getSlashBladeBlendLuminous(Identifier texture) {
        return slashBladeBlendLuminousCache.computeIfAbsent(texture, t -> {
            RenderPipeline pipeline = RenderPipeline.builder(ENTITY_EMISSIVE_SNIPPET)
                    .withLocation("pipeline/entity_translucent_emissive")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withCull(false)
                    .withSampler("Sampler1")
                    .withColorTargetState(new ColorTargetState(LIGHTNING_ADDITIVE_TRANSPARENCY))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", t)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_blend_luminous_" + t, state);
        });
    }

    public static RenderType getChargeEffect(Identifier texture, float x, float y) {
        ChargeEffectKey key = new ChargeEffectKey(texture, x, y);
        return chargeEffectCache.computeIfAbsent(key, k -> {
            RenderPipeline pipeline = RenderPipeline.builder(MATRICES_FOG_SNIPPET)
                    .withLocation("pipeline/energy_swirl")
                    .withVertexShader("core/entity")
                    .withFragmentShader("core/entity")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("EMISSIVE")
                    .withShaderDefine("NO_OVERLAY")
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withShaderDefine("APPLY_TEXTURE_MATRIX")
                    .withSampler("Sampler0")
                    .withColorTargetState(new ColorTargetState(LIGHTNING_ADDITIVE_TRANSPARENCY))
                    .withCull(false)
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", k.texture)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .setTextureTransform(new TextureTransform.OffsetTextureTransform(k.x, k.y))
                    .useLightmap()
                    .useOverlay()
                    .sortOnUpload()
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_charge_effect_" + k.texture + "_" + k.x + "_" + k.y, state);
        });
    }

    public static RenderType getSlashBladeBlendLuminousDepthWrite(Identifier texture) {
        return luminousDepthWriteCache.computeIfAbsent(texture, t -> {
            RenderPipeline pipeline = RenderPipeline.builder(ENTITY_EMISSIVE_SNIPPET)
                    .withLocation("pipeline/entity_translucent_emissive")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withColorTargetState(new ColorTargetState(LIGHTNING_ADDITIVE_TRANSPARENCY))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", t)
                    .setOutputTarget(new OutputTarget("particles_target", () -> Minecraft.getInstance().levelRenderer.getParticlesTarget()))
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_blend_luminous_depth_write_" + t, state);
        });
    }

    protected static final BlendFunction LIGHTNING_REVERSE_TRANSPARENCY = new BlendFunction(
            SourceFactor.SRC_ALPHA, DestFactor.ONE,
            SourceFactor.ONE, DestFactor.ONE);

    public static RenderType getSlashBladeBlendReverseLuminous(Identifier texture) {
        return reverseLuminousCache.computeIfAbsent(texture, t -> {
            RenderPipeline pipeline = RenderPipeline.builder(ENTITY_EMISSIVE_SNIPPET)
                    .withLocation("pipeline/entity_translucent_emissive")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withColorTargetState(new ColorTargetState(LIGHTNING_REVERSE_TRANSPARENCY))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
                    .build();

            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", t)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .bufferSize(256)
                    .createRenderSetup();

            return RenderType.create("slashblade_blend_reverse_luminous_" + t, state);
        });
    }

    private static class ChargeEffectKey {
        final Identifier texture;
        final float x;
        final float y;

        ChargeEffectKey(Identifier texture, float x, float y) {
            this.texture = texture;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChargeEffectKey that = (ChargeEffectKey) o;
            return Float.compare(that.x, x) == 0 &&
                    Float.compare(that.y, y) == 0 &&
                    texture.equals(that.texture);
        }

        @Override
        public int hashCode() {
            int result = texture.hashCode();
            result = 31 * result + Float.floatToIntBits(x);
            result = 31 * result + Float.floatToIntBits(y);
            return result;
        }
    }
}
