package mods.flammpfeil.slashblade.client.renderer.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BladeRenderState extends RenderStateShard {

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

    public BladeRenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
    }

    public static void renderOverrided(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture,
                                       PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn,
                packedLightIn, BladeRenderState::getSlashBladeBlend, true);
    }

    public static void renderOverridedColorWrite(ItemStack stack, WavefrontObject model, String target,
                                                 ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn,
                BladeRenderState::getSlashBladeBlendColorWrite, true);
    }

    public static void renderChargeEffect(ItemStack stack, float f, WavefrontObject model, String target,
                                          ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn,
                (loc) -> BladeRenderState.getChargeEffect(loc, f * 0.1F % 1.0F, f * 0.01F % 1.0F), false);
    }

    public static void renderOverridedLuminous(ItemStack stack, WavefrontObject model, String target,
                                               ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn,
                BladeRenderState::getSlashBladeBlendLuminous, false);
    }

    public static void renderOverridedLuminousDepthWrite(ItemStack stack, WavefrontObject model, String target,
                                                         ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn,
                BladeRenderState::getSlashBladeBlendLuminousDepthWrite, false);
    }

    public static void renderOverridedReverseLuminous(ItemStack stack, WavefrontObject model, String target,
                                                      ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        renderOverrided(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn,
                BladeRenderState::getSlashBladeBlendReverseLuminous, false);
    }

    public static void renderOverrided(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture,
                                       PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                                       Function<ResourceLocation, RenderType> getRenderType, boolean enableEffect) {
        RenderOverrideEvent event = RenderOverrideEvent.onRenderOverride(stack, model, target, texture, matrixStackIn,
                bufferIn, packedLightIn);

        if (event.isCanceled())
            return;

        ResourceLocation loc = event.getTexture();

        RenderType rt = getRenderType.apply(loc);// getSlashBladeBlendLuminous(event.getTexture());
        VertexConsumer vb = bufferIn.getBuffer(rt);

        Face.setCol(col);
        Face.setLightMap(packedLightIn);
        Face.setMatrix(matrixStackIn);
        event.getModel().tessellateOnly(vb, event.getTarget());

        if (stack.hasFoil() && enableEffect) {
            vb = bufferIn.getBuffer(target.startsWith("item_") ? BladeRenderState.SLASHBLADE_ITEM_GLINT : BladeRenderState.SLASHBLADE_GLINT);
            event.getModel().tessellateOnly(vb, event.getTarget());
        }

        Face.resetMatrix();
        Face.resetLightMap();
        Face.resetCol();

        Face.resetAlphaOverride();
        Face.resetUvOperator();

        resetCol();
    }

    private static final Map<ResourceLocation, RenderType> slashBladeBlendCache = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> slashBladeBlendColorWriteCache = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> slashBladeBlendLuminousCache = new HashMap<>();
    private static final Map<ChargeEffectKey, RenderType> chargeEffectCache = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> luminousDepthWriteCache = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> reverseLuminousCache = new HashMap<>();

    public static RenderType getSlashBladeBlend(ResourceLocation texture) {
        return slashBladeBlendCache.computeIfAbsent(texture, t -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setTextureState(new RenderStateShard.TextureStateShard(t, false, true))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true);

            return RenderType.create("slashblade_blend_" + t, DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.TRIANGLES, 256, true, false, state);
        });
    }

    public static final RenderType SLASHBLADE_GLINT = BladeRenderState.getSlashBladeGlint();

    public static RenderType getSlashBladeGlint() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false))
                .setWriteMaskState(COLOR_WRITE)
                .setCullState(NO_CULL)
                .setDepthTestState(EQUAL_DEPTH_TEST)
                .setTransparencyState(GLINT_TRANSPARENCY)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setTexturingState(ENTITY_GLINT_TEXTURING)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return RenderType.create("slashblade_glint", DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }

    public static final RenderType SLASHBLADE_ITEM_GLINT = BladeRenderState.getSlashBladeItemGlint();

    public static RenderType getSlashBladeItemGlint() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                .setWriteMaskState(COLOR_WRITE)
                .setCullState(NO_CULL)
                .setDepthTestState(EQUAL_DEPTH_TEST)
                .setTransparencyState(GLINT_TRANSPARENCY)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setTexturingState(GLINT_TEXTURING)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return RenderType.create("slashblade_glint", DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.TRIANGLES, 256, true, false, state);
    }

    public static RenderType getSlashBladeBlendColorWrite(ResourceLocation texture) {
        return slashBladeBlendColorWriteCache.computeIfAbsent(texture, t -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setTextureState(new RenderStateShard.TextureStateShard(t, false, true))
                    .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);

            return RenderType.create("slashblade_blend_write_color_" + t, DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.TRIANGLES, 256, false, true, state);
        });
    }

    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            "lightning_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType getSlashBladeBlendLuminous(ResourceLocation texture) {
        return slashBladeBlendLuminousCache.computeIfAbsent(texture, t -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(new RenderStateShard.TextureStateShard(t, true, true))
                    .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);

            return RenderType.create("slashblade_blend_luminous_" + t, DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.TRIANGLES, 256, false, true, state);
        });
    }

    public static RenderType getChargeEffect(ResourceLocation texture, float x, float y) {
        ChargeEffectKey key = new ChargeEffectKey(texture, x, y);
        return chargeEffectCache.computeIfAbsent(key, k -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setTextureState(new RenderStateShard.TextureStateShard(k.texture, false, true))
                    .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(k.x, k.y))
                    .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false);

            return RenderType.create("slashblade_charge_effect_" + k.texture + "_" + k.x + "_" + k.y,
                    DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, state);
        });
    }

    public static RenderType getSlashBladeBlendLuminousDepthWrite(ResourceLocation texture) {
        return luminousDepthWriteCache.computeIfAbsent(texture, t -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .setTextureState(new RenderStateShard.TextureStateShard(t, true, true))
                    .setTransparencyState(LIGHTNING_ADDITIVE_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false);

            return RenderType.create("slashblade_blend_luminous_depth_write_" + t,
                    DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, state);
        });
    }

    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_REVERSE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            "lightning_reverse_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
    }, () -> {
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType getSlashBladeBlendReverseLuminous(ResourceLocation texture) {
        return reverseLuminousCache.computeIfAbsent(texture, t -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setTextureState(new RenderStateShard.TextureStateShard(t, true, true))
                    .setTransparencyState(LIGHTNING_REVERSE_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false);

            return RenderType.create("slashblade_blend_reverse_luminous_" + t,
                    DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, state);
        });
    }

    private static class ChargeEffectKey {
        final ResourceLocation texture;
        final float x;
        final float y;

        ChargeEffectKey(ResourceLocation texture, float x, float y) {
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
