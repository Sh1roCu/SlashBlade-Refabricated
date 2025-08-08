package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.event.RenderPlayerEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SneakingMotionCanceller {
    private static final class SingletonHolder {
        private static final SneakingMotionCanceller instance = new SneakingMotionCanceller();
    }

    public static SneakingMotionCanceller getInstance() {
        return SingletonHolder.instance;
    }

    private SneakingMotionCanceller() {
    }

    public void register() {
        RenderPlayerEvents.PRE.register((Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) -> {
            this.onRenderPlayerEventPre(player, renderer, partialTick, poseStack, buffer, packedLight);
            return false;
        });
    }

    public void onRenderPlayerEventPre(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(stack).isEmpty())
            return;

        if (!renderer.getModel().crouching)
            return;

        final Minecraft instance = Minecraft.getInstance();
        if (instance.options.getCameraType() == CameraType.FIRST_PERSON && instance.player == player)
            return;

        renderer.getModel().crouching = false;

        Vec3 offset = renderer
                .getRenderOffset((AbstractClientPlayer) player, packedLight).scale(-1);

        poseStack.translate(offset.x, offset.y, offset.z);
    }
}
