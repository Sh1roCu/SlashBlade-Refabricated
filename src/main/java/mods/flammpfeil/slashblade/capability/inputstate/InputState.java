package mods.flammpfeil.slashblade.capability.inputstate;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.event.Scheduler;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.EnumMap;
import java.util.EnumSet;

public class InputState implements IInputState {
    static final String KEY = "Command";

    EnumSet<InputCommand> commands = EnumSet.noneOf(InputCommand.class);
    Scheduler scheduler = new Scheduler();
    EnumMap<InputCommand, Long> lastPressTimes = Maps.newEnumMap(InputCommand.class);

    @Override
    public EnumSet<InputCommand> getCommands() {
        return commands;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public EnumMap<InputCommand, Long> getLastPressTimes() {
        return lastPressTimes;
    }

    @Override
    public void readData(ValueInput readView) {
        getCommands().clear();
        getCommands().addAll(EnumSetConverter.convertToEnumSet(InputCommand.class, readView.getIntOr(KEY, 0)));
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putInt(KEY, EnumSetConverter.convertToInt(getCommands()));
    }
}
