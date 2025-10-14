package mods.flammpfeil.slashblade.event;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BladeMotionEvent extends BaseEvent implements ICancellableEvent {
    private final LivingEntity entity;
    private ResourceLocation combo;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onBladeMotion(event);
        }
    });

    public interface Callback {
        void onBladeMotion(BladeMotionEvent event);
    }

    public BladeMotionEvent(LivingEntity entity, ResourceLocation combo) {
        this.entity = entity;
        this.combo = combo;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ResourceLocation getCombo() {
        return this.combo;
    }

    public void setCombo(ResourceLocation combo) {
        this.combo = combo;
    }
}
