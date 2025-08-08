package mods.flammpfeil.slashblade.network;

import cn.sh1rocu.slashblade.api.extension.IEntityAdditionalSpawnData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class NetworkManager {
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(MoveCommandMessage.ID, MoveCommandMessage::handle);
    }

    @Environment(EnvType.CLIENT)
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(RankSyncMessage.ID, RankSyncMessage::handle);
        ClientPlayNetworking.registerGlobalReceiver(MotionBroadcastMessage.ID, MotionBroadcastMessage::handle);
        ClientPlayNetworking.registerGlobalReceiver(IEntityAdditionalSpawnData.EXTRA_DATA_PACKET, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            buf.retain();
            client.execute(() -> {
                Entity entity = Objects.requireNonNull(client.level).getEntity(entityId);
                if (entity instanceof IEntityAdditionalSpawnData extra) {
                    extra.readSpawnData(buf);
                }
                buf.release();
            });
        });
    }
}
