package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record RankSyncPacket(long rawPoint) implements CustomPacketPayload {
    private static final Identifier ID = SlashBlade.prefix("s2c_rank_sync");
    public static final Type<RankSyncPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RankSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            RankSyncPacket::rawPoint,
            RankSyncPacket::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(RankSyncPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> setPoint(payload.rawPoint));
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}