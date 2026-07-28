package mods.flammpfeil.slashblade.network;

import cn.sh1rocu.slashblade.util.network.AdvancedAddEntityPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NetworkManager {
    public static void registerC2SPackets() {
        PayloadTypeRegistry.serverboundPlay().register(MoveCommandPacket.TYPE, MoveCommandPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(MoveCommandPacket.TYPE, MoveCommandPacket::handle);
    }

    public static void registerS2CPackets() {
        PayloadTypeRegistry.clientboundPlay().register(AdvancedAddEntityPayload.TYPE, AdvancedAddEntityPayload.STREAM_CODEC);

        PayloadTypeRegistry.clientboundPlay().register(RankSyncPacket.TYPE, RankSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MotionBroadcastPacket.TYPE, MotionBroadcastPacket.STREAM_CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(AdvancedAddEntityPayload.TYPE, AdvancedAddEntityPayload::handle);

        ClientPlayNetworking.registerGlobalReceiver(RankSyncPacket.TYPE, RankSyncPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(MotionBroadcastPacket.TYPE, MotionBroadcastPacket::handle);
    }
}
