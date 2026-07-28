package mods.flammpfeil.slashblade.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.SlashBladeTEISR;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

public class BladeFixedRenderer extends BladeBaseRenderer {
    @Override
    protected void submitInner(BladeItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.translate(0.5f, 0.5f, 0.5f);

        if (state.isFramed && state.bladeStandState != null) {
            SlashBladeTEISR.renderModel(state, poseStack, submitNodeCollector, lightCoords);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            SlashBladeTEISR.renderIcon(state, poseStack, submitNodeCollector, lightCoords, 0.0095f);
        }
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<BladeItemRenderState> {
        public static final Identifier ID = SlashBlade.prefix("blade_fixed");
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public BladeFixedRenderer bake(BakingContext context) {
            return new BladeFixedRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
