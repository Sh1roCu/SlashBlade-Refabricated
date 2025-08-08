package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.inputstate.IInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.SlashBladeKeyMappings;
import mods.flammpfeil.slashblade.network.MoveCommandMessage;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

        if (player.getMainHandItem().isEmpty() || CapabilitySlashBlade.BLADESTATE.maybeGet(player.getMainHandItem()).isEmpty())
            return;

        EnumSet<InputCommand> commands = EnumSet.noneOf(InputCommand.class);

        if (player.input.up)
            commands.add(InputCommand.FORWARD);
        if (player.input.down)
            commands.add(InputCommand.BACK);
        if (player.input.left)
            commands.add(InputCommand.LEFT);
        if (player.input.right)
            commands.add(InputCommand.RIGHT);

        if (player.input.shiftKeyDown)
            commands.add(InputCommand.SNEAK);

        if (player.input.jumping) {
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

        Level worldIn = player.getCommandSenderWorld();

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
            MoveCommandMessage.send(EnumSetConverter.convertToInt(commands));
        }
    }
}