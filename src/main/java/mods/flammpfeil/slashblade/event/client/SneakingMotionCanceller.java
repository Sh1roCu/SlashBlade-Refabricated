package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import cn.sh1rocu.slashblade.api.event.RenderPlayerEvent;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
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
        RenderPlayerEvent.PRE.register(this::onRenderPlayerEventPre);
    }

    @SuppressWarnings("rawtypes")
    public void onRenderPlayerEventPre(RenderPlayerEvent.Pre event) {
        AvatarRenderState player = (AvatarRenderState) event.getRenderState();
        var renderer = event.getRenderer();
        ItemStack stack = player.getMainHandItemStack();

        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return;

        if (!player.isCrouching)
            return;

        final Minecraft instance = Minecraft.getInstance();
        if (instance.options.getCameraType() == CameraType.FIRST_PERSON &&
                instance.player.getId() == player.getDataOrDefault(RenderStateKeys.ENTITY_ID, -99))
            return;

        player.isCrouching = false;

        Vec3 offset = renderer
                .getRenderOffset(player).scale(-1);

        event.getPoseStack().translate(offset.x, offset.y, offset.z);
    }
}
