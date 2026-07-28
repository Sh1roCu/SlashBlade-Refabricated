package cn.sh1rocu.slashblade.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundUtil {
    public static void playNotifySound(ServerPlayer player, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        player.connection.send(new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundSource,
                        player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()
                )
        );
    }
}
