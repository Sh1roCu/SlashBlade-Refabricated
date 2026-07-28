package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record MotionBroadcastPacket(UUID playerId, String combo) implements CustomPacketPayload {
    private static final Identifier ID = SlashBlade.prefix("s2c_motion_broadcast");
    public static final Type<MotionBroadcastPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, MotionBroadcastPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            MotionBroadcastPacket::playerId,
            ByteBufCodecs.STRING_UTF8,
            MotionBroadcastPacket::combo,
            MotionBroadcastPacket::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(MotionBroadcastPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> setPoint(payload.playerId, payload.combo));
    }

    @Environment(EnvType.CLIENT)
    public static void setPoint(UUID playerId, String combo) {
        if (Minecraft.getInstance().level == null) return;
        Player target = Minecraft.getInstance().level.getPlayerByUUID(playerId);

        if (target == null)
            return;
        if (!(target instanceof AbstractClientPlayer))
            return;

        Identifier state = Identifier.tryParse(combo);
        if (state == null || !ComboStateRegistry.COMBO_STATE.containsKey(state))
            return;

        BladeMotionEvent.EVENT.invoker().onBladeMotion(new BladeMotionEvent(target, state));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}