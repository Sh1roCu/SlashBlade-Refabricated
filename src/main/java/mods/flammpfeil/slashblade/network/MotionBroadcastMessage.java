package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.UUID;

public class MotionBroadcastMessage {
    public static final ResourceLocation ID = SlashBlade.prefix("s2c_motion_broadcast");

    public static void send(UUID playerId, String combo, Collection<ServerPlayer> players) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(playerId);
        buf.writeUtf(combo);
        players.forEach(player -> ServerPlayNetworking.send(player, ID, buf));
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        UUID playerId = buf.readUUID();
        String combo = buf.readUtf();
        client.execute(() -> setPoint(playerId, combo));
    }

    @Environment(EnvType.CLIENT)
    public static void setPoint(UUID playerId, String combo) {
        Player target = Minecraft.getInstance().level.getPlayerByUUID(playerId);

        if (target == null)
            return;
        if (!(target instanceof AbstractClientPlayer))
            return;

        ResourceLocation state = ResourceLocation.tryParse(combo);
        if (state == null || !ComboStateRegistry.COMBO_STATE.containsKey(state))
            return;

        BladeMotionEvent.CALLBACK.invoker().onBladeMotion(new BladeMotionEvent(target, state));
    }
}