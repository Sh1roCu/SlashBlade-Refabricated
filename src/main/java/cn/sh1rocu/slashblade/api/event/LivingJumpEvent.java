package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public class LivingJumpEvent extends LivingEvent {
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
        for (Callback callback : callbacks)
            callback.onLivingEntityJump(event);
    });

    public LivingJumpEvent(LivingEntity e) {
        super(e);
    }

    public interface Callback {
        void onLivingEntityJump(LivingJumpEvent event);
    }
}