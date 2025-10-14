package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.network.MotionBroadcastPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class BladeMotionEventBroadcaster {

    private static final class SingletonHolder {
        private static final BladeMotionEventBroadcaster instance = new BladeMotionEventBroadcaster();
    }

    public static BladeMotionEventBroadcaster getInstance() {
        return SingletonHolder.instance;
    }

    private BladeMotionEventBroadcaster() {
    }

    public void register() {
        BladeMotionEvent.CALLBACK.register(this::onBladeMotion);
    }

    public void onBladeMotion(BladeMotionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp))
            return;

        // if(msg.combo == Extra.EX_JUDGEMENT_CUT.getName())
        {
            for (ServerPlayer player : PlayerLookup.around(sp.serverLevel(), new Vec3(
                    sp.getX(),
                    sp.getY(),
                    sp.getZ()
            ), 20)) {
                ServerPlayNetworking.send(player, new MotionBroadcastPacket(sp.getUUID(), event.getCombo().toString()));
            }
        }

    }
}
