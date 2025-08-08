package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingAttackEvent extends LivingEvent implements ICancellableEvent {
    private final DamageSource source;
    private final float amount;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback e : callbacks)
            e.onLivingAttack(event);
    });

    public LivingAttackEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public interface Callback {
        void onLivingAttack(LivingAttackEvent event);
    }
}