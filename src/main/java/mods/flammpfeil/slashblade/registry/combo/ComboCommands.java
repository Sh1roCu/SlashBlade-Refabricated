package mods.flammpfeil.slashblade.registry.combo;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ComboCommands {
    public static final EnumSet<InputCommand> COMBO_B1_ALT = EnumSet.of(InputCommand.BACK, InputCommand.R_DOWN);
    private static final Map<EnumSet<InputCommand>, ResourceLocation> DEAFULT_STANDBY = new HashMap<>();

    public static ResourceLocation initStandByCommand(LivingEntity a) {
        return initStandByCommand(a, DEAFULT_STANDBY);
    }

    public static ResourceLocation initStandByCommand(LivingEntity a,
                                                      Map<EnumSet<InputCommand>, ResourceLocation> map) {
        EnumSet<InputCommand> commands = CapabilityInputState.INPUT_STATE.maybeGet(a)
                .map((state) -> state.getCommands(a)).orElseGet(() -> EnumSet.noneOf(InputCommand.class));

        return map.entrySet().stream().filter((entry) -> commands.containsAll(entry.getKey()))
                // .findFirst()
                .min(Comparator.comparingInt(
                        (entry) -> ComboStateRegistry.COMBO_STATE.get(entry.getValue()).getPriority()))
                .map(Map.Entry::getValue).orElseGet(() -> ComboStateRegistry.getId(ComboStateRegistry.NONE));
    }

    public static void initDefaultStandByCommands() {
        DEAFULT_STANDBY.put(
                EnumSet.of(InputCommand.ON_GROUND, InputCommand.SNEAK, InputCommand.FORWARD, InputCommand.R_CLICK),
                ComboStateRegistry.getId(ComboStateRegistry.RAPID_SLASH));
        DEAFULT_STANDBY.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.L_CLICK),
                ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1));
        DEAFULT_STANDBY.put(
                EnumSet.of(InputCommand.ON_GROUND, InputCommand.BACK, InputCommand.SNEAK, InputCommand.R_CLICK),
                ComboStateRegistry.getId(ComboStateRegistry.UPPERSLASH));

        DEAFULT_STANDBY.put(EnumSet.of(InputCommand.ON_GROUND, InputCommand.R_CLICK),
                ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1));

        DEAFULT_STANDBY.put(
                EnumSet.of(InputCommand.ON_AIR, InputCommand.SNEAK, InputCommand.BACK, InputCommand.R_CLICK),
                ComboStateRegistry.getId(ComboStateRegistry.AERIAL_CLEAVE));
        DEAFULT_STANDBY.put(EnumSet.of(InputCommand.ON_AIR), ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_A1));
    }
}
