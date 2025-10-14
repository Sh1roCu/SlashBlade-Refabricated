package cn.sh1rocu.slashblade.api.event;

import net.minecraft.world.entity.LivingEntity;

public class LivingEvent extends BaseEvent {
    private final LivingEntity livingEntity;

    public LivingEvent(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public LivingEntity getEntity() {
        return livingEntity;
    }
}
