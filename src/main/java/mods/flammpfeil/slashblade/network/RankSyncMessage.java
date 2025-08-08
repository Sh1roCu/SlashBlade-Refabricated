package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class RankSyncMessage {
    public static final ResourceLocation ID = SlashBlade.prefix("s2c_rank_sync");

    public static void send(long rawPoint, Collection<ServerPlayer> players) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeLong(rawPoint);
        players.forEach(player -> ServerPlayNetworking.send(player, ID, buf));
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        long rawPoint = buf.readLong();
        client.execute(() -> setPoint(rawPoint));
    }

    @Environment(EnvType.CLIENT)
    public static void setPoint(long point) {
        Player pl = Minecraft.getInstance().player;
        CapabilityConcentrationRank.RANK_POINT.maybeGet(pl).ifPresent(cr -> {

            long time = pl.level().getGameTime();

            IConcentrationRank.ConcentrationRanks oldRank = cr.getRank(time);

            cr.setRawRankPoint(point);
            cr.setLastUpdate(time);

            if (oldRank.level < cr.getRank(time).level)
                cr.setLastRankRise(time);
        });
    }
}