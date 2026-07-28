package mods.flammpfeil.slashblade.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class BladeNoDataRenderer implements NoDataSpecialModelRenderer {
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {

    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {

    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Void> {
        public static final Identifier ID = SlashBlade.prefix("blade_no_data");
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public BladeNoDataRenderer bake(BakingContext context) {
            return new BladeNoDataRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
