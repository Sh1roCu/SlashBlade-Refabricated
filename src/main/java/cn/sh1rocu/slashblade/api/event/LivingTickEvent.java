package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public class LivingTickEvent extends LivingEvent implements ICancellableEvent {
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
        for (Callback callback : callbacks)
            callback.onLivingTick(event);
    });

    public LivingTickEvent(LivingEntity e) {
        super(e);
    }

    public interface Callback {
        void onLivingTick(LivingTickEvent event);
    }
}