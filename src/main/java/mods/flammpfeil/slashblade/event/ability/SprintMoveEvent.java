package mods.flammpfeil.slashblade.event.ability;


import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumSet;

/**
 * 特殊移动事件，V键冲刺时触发，取消事件可以阻止冲刺发生
 *
 * @author Arcomit
 * @since 2026-05-04
 */
public class SprintMoveEvent extends BaseEvent implements ICancellableEvent {
    private final ServerPlayer player;
    private final EnumSet<InputCommand> currentCommands;

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public SprintMoveEvent(ServerPlayer player, EnumSet<InputCommand> currentCommands) {
        this.player = player;
        this.currentCommands = currentCommands;
    }

    public EnumSet<InputCommand> getCommands() {
        return currentCommands;
    }

    public interface Callback {
        void post(SprintMoveEvent event);
    }
}