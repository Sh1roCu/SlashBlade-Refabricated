package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.inputstate.IInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.SlashBladeKeyMappings;
import mods.flammpfeil.slashblade.network.MoveCommandPacket;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class MoveInputHandler {
    public static final String LAST_CHANGE_TIME = "SB_LAST_CHANGE_TIME";

    public static boolean checkFlag(int data, int flags) {
        return (data & flags) == flags;
    }

    @Environment(EnvType.CLIENT)
    public static void onPlayerPostTick(Minecraft client) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        if (player.getMainHandItem().isEmpty() || CapabilitySlashBlade.getBladeState(player.getMainHandItem()).isEmpty())
            return;

        EnumSet<InputCommand> commands = EnumSet.noneOf(InputCommand.class);

        var input = player.input.keyPresses;
        if (input.forward())
            commands.add(InputCommand.FORWARD);
        if (input.backward())
            commands.add(InputCommand.BACK);
        if (input.left())
            commands.add(InputCommand.LEFT);
        if (input.right())
            commands.add(InputCommand.RIGHT);

        if (input.shift())
            commands.add(InputCommand.SNEAK);

        if (input.jump()) {
            commands.add(InputCommand.JUMP);
        }

        final Minecraft minecraftInstance = Minecraft.getInstance();

        if (SlashBladeKeyMappings.KEY_SPECIAL_MOVE.isDown())
            commands.add(InputCommand.SPRINT);

        if (minecraftInstance.options.keyUse.isDown())
            commands.add(InputCommand.R_DOWN);
        if (minecraftInstance.options.keyAttack.isDown())
            commands.add(InputCommand.L_DOWN);

        if (SlashBladeKeyMappings.KEY_SUMMON_BLADE.isDown())
            commands.add(InputCommand.M_DOWN);

        EnumSet<InputCommand> old = CapabilityInputState.INPUT_STATE.maybeGet(player).map(IInputState::getCommands)
                .orElseGet(() -> EnumSet.noneOf(InputCommand.class));

        Level worldIn = player.level();

        long currentTime = worldIn.getGameTime();
        boolean doSend = !old.equals(commands);

        if (doSend) {
            CapabilityInputState.INPUT_STATE.maybeGet(player).ifPresent((state) -> {
                commands.forEach(c -> {
                    if (!old.contains(c))
                        state.getLastPressTimes().put(c, currentTime);
                });

                state.getCommands().clear();
                state.getCommands().addAll(commands);
            });
            ClientPlayNetworking.send(new MoveCommandPacket(EnumSetConverter.convertToInt(commands)));
        }
    }
}