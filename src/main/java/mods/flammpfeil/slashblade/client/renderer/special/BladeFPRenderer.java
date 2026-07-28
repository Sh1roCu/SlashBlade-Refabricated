package mods.flammpfeil.slashblade.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeFirstPersonRender;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

public class BladeFPRenderer extends BladeBaseRenderer {
    public static class LeftHand extends BladeFPRenderer {
        @Override
        protected void submitInner(BladeItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
            if (BladeModel.user.getMainArm() == HumanoidArm.LEFT) {
                BladeFirstPersonRender.getInstance().render(poseStack, submitNodeCollector, lightCoords);
            }
        }

        public record Unbaked() implements SpecialModelRenderer.Unbaked<BladeItemRenderState> {
            public static final Identifier ID = SlashBlade.prefix("blade_fp_left");
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

            @Override
            public LeftHand bake(BakingContext context) {
                return new LeftHand();
            }

            @Override
            public MapCodec<Unbaked> type() {
                return MAP_CODEC;
            }
        }
    }

    public static class RightHand extends BladeFPRenderer {
        @Override
        protected void submitInner(BladeItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
            if (BladeModel.user.getMainArm() == HumanoidArm.RIGHT) {
                BladeFirstPersonRender.getInstance().render(poseStack, submitNodeCollector, lightCoords);
            }
        }

        public record Unbaked() implements SpecialModelRenderer.Unbaked<BladeItemRenderState> {
            public static final Identifier ID = SlashBlade.prefix("blade_fp_right");
            public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

            @Override
            public RightHand bake(BakingContext context) {
                return new RightHand();
            }

            @Override
            public MapCodec<Unbaked> type() {
                return MAP_CODEC;
            }
        }
    }

}
