package mods.flammpfeil.slashblade.client.renderer.special;

import cn.sh1rocu.slashblade.api.extension.IEntityRepresentation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.SlashBladeTEISR;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.Optional;
import java.util.function.Consumer;

public class BladeBaseRenderer implements SpecialModelRenderer<BladeItemRenderState> {

    @Override
    public void submit(BladeItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (BladeModel.user == null) {
            final Minecraft minecraftInstance = Minecraft.getInstance();
            BladeModel.user = minecraftInstance.player;
        }

        submitInner(state, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
    }

    protected void submitInner(BladeItemRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        SlashBladeTEISR.renderIcon(state, poseStack, submitNodeCollector, lightCoords, 0.0095f);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {

    }

    @Override
    public BladeItemRenderState extractArgument(ItemStack stack) {
        return createBladeItemRenderState(stack);
    }

    public static BladeItemRenderState createBladeItemRenderState(ItemStack stack) {
        BladeItemRenderState renderState = new BladeItemRenderState();
        renderState.swordTypes = SwordType.from(stack);
        Optional<ISlashBladeState> cap = CapabilitySlashBlade.getBladeState(stack);
        renderState.modelLocation = cap.flatMap(ISlashBladeState::getModel).orElseGet(() -> SlashBladeTEISR.stackDefaultModel(stack));
        renderState.textureLocation = cap.flatMap(ISlashBladeState::getTexture).orElseGet(() -> SlashBladeTEISR.stackDefaultTexture(stack));
        renderState.hasFoil = stack.hasFoil();
        renderState.damageValue = stack.getDamageValue();
        renderState.maxDamage = stack.getMaxDamage();
        ItemFrame frame = ((IEntityRepresentation) (Object) stack).sb$getFrame();
        if (frame instanceof BladeStandEntity bladeStand) {
            renderState.isFramed = true;
            BladeItemRenderState.BladeStandState bladeStandState = new BladeItemRenderState.BladeStandState();
            bladeStandState.currentType = bladeStand.currentType;
            bladeStandState.pose = bladeStand.getPose();
            renderState.bladeStandState = bladeStandState;
        }

        return renderState;
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<BladeItemRenderState> {
        public static final Identifier ID = SlashBlade.prefix("blade_base");
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public BladeBaseRenderer bake(BakingContext context) {
            return new BladeBaseRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
