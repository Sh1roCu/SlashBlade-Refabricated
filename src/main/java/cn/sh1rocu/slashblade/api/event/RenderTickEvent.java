package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Timer;

public class RenderTickEvent extends BaseEvent {
    private final Timer timer;

    public RenderTickEvent(Timer timer) {
        this.timer = timer;
    }

    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> event -> {
        for (Start callback : callbacks) {
            callback.onStart(event);
        }
    });
    public static final Event<End> END = EventFactory.createArrayBacked(End.class, callbacks -> event -> {
        for (End callback : callbacks) {
            callback.onEnd(event);
        }
    });

    public interface Start {
        void onStart(Pre event);
    }

    public interface End {
        void onEnd(Post event);
    }

    public static final class Pre extends RenderTickEvent {
        public Pre(Timer timer) {
            super(timer);
        }
    }

    public static final class Post extends RenderTickEvent {
        public Post(Timer timer) {
            super(timer);
        }
    }
}
