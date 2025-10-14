package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import mods.flammpfeil.slashblade.capability.inputstate.IInputState;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumSet;

public class InputCommandEvent extends BaseEvent {

    public InputCommandEvent(ServerPlayer player, IInputState state, EnumSet<InputCommand> old,
                             EnumSet<InputCommand> current) {
        this.player = player;
        this.state = state;
        this.old = old;
        this.current = current;
    }

    public ServerPlayer getEntity() {
        return player;
    }

    public IInputState getState() {
        return state;
    }

    public EnumSet<InputCommand> getOld() {
        return old;
    }

    public EnumSet<InputCommand> getCurrent() {
        return current;
    }

    ServerPlayer player;
    IInputState state;
    EnumSet<InputCommand> old;
    EnumSet<InputCommand> current;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onInput(event);
        }
    });

    public interface Callback {
        void onInput(InputCommandEvent event);
    }

    public static InputCommandEvent onInputChange(ServerPlayer player, IInputState state, EnumSet<InputCommand> old,
                                                  EnumSet<InputCommand> current) {
        InputCommandEvent event = new InputCommandEvent(player, state, old, current);

        CALLBACK.invoker().onInput(event);
        return event;
    }
}
