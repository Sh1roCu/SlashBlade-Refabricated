package mods.flammpfeil.slashblade.capability.inputstate;

import dev.onyxstudios.cca.api.v3.component.Component;
import mods.flammpfeil.slashblade.event.Scheduler;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumMap;
import java.util.EnumSet;

public interface IInputState extends Component {
    EnumSet<InputCommand> getCommands();

    Scheduler getScheduler();

    EnumMap<InputCommand, Long> getLastPressTimes();

    default Long getLastPressTime(InputCommand command) {
        return this.getLastPressTimes().getOrDefault(command, -1L);
    }

    default EnumSet<InputCommand> getCommands(LivingEntity owner) {
        EnumSet<InputCommand> commands = getCommands().clone();

        if (owner.onGround()) {
            commands.add(InputCommand.ON_GROUND);
            // commands.remove(InputCommand.ON_AIR);
        } else {
            commands.add(InputCommand.ON_AIR);
            // commands.remove(InputCommand.ON_GROUND);
        }
        return commands;
    }
}
