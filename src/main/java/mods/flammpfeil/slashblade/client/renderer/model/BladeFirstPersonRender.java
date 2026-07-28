package mods.flammpfeil.slashblade.client.renderer.model;

import cn.sh1rocu.slashblade.mixin.accessor.MultiPlayerGameModeAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.compat.iris.IrisCompat;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeFirstPersonRender {
    private LayerMainBlade<EntityRenderState, ?> layer = null;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BladeFirstPersonRender() {
        Minecraft mc = Minecraft.getInstance();

        EntityRenderer<?, ?> renderer = mc.getEntityRenderDispatcher().getRenderer(mc.player);
        if (renderer instanceof RenderLayerParent parent)
            layer = new LayerMainBlade(parent);
    }

    private static final class SingletonHolder {
        private static final BladeFirstPersonRender instance = new BladeFirstPersonRender();
    }

    public static BladeFirstPersonRender getInstance() {
        return SingletonHolder.instance;
    }

    public void render(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int combinedLightIn) {
        if (layer == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        boolean flag = mc.getCameraEntity() instanceof LivingEntity
                && ((LivingEntity) mc.getCameraEntity()).isSleeping();
        if (!(mc.options.getCameraType() == CameraType.FIRST_PERSON && !flag && !mc.options.hideGui
                && !(((MultiPlayerGameModeAccessor) mc.gameMode).sb$getLocalPlayerMode() == GameType.SPECTATOR))) {
            return;
        }
        LocalPlayer player = mc.player;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return;

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            PoseStack.Pose me = matrixStack.last();
            me.pose().identity();
            me.normal().identity();

            float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);

            if (IrisCompat.isUsingRenderPack()) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(player.getXRot()));
            } else {
                matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - Mth.lerp(partialTicks, player.yRotO, player.getYRot())));
            }

            matrixStack.translate(0.0f, 0.0f, -0.5f);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrixStack.scale(1.2F, 1.0F, 1.0F);

            // no sync pitch
            matrixStack.mulPose(Axis.XP.rotationDegrees(-Mth.clamp(player.getXRot(), -60F, 10F)));

            // layer.disableOffhandRendering();
            layer.submit(matrixStack, submitNodeCollector, combinedLightIn, mc.getEntityRenderDispatcher().extractEntity(mc.player, partialTicks), 0, 0);
        }
    }
}