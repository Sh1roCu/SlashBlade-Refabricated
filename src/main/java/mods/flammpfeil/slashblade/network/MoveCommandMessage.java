package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class MoveCommandMessage {
    public static final ResourceLocation ID = SlashBlade.prefix("c2s_move_command");

    @Environment(EnvType.CLIENT)
    public static void send(int command) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(command);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int command = buf.readInt();
        server.execute(() -> {
            // do stuff
            ItemStack stack = sender.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.isEmpty())
                return;
            if (CapabilitySlashBlade.BLADESTATE.maybeGet(stack).isEmpty())
                return;

            CapabilityInputState.INPUT_STATE.maybeGet(sender).ifPresent((state) -> {
                EnumSet<InputCommand> old = state.getCommands().clone();

                state.getCommands().clear();
                state.getCommands().addAll(EnumSetConverter.convertToEnumSet(InputCommand.class, command));

                EnumSet<InputCommand> current = state.getCommands().clone();

                long currentTime = sender.level().getGameTime();
                current.forEach(c -> {
                    if (!old.contains(c))
                        state.getLastPressTimes().put(c, currentTime);
                });

                InputCommandEvent.onInputChange(sender, state, old, current);

            });
        });
    }
}