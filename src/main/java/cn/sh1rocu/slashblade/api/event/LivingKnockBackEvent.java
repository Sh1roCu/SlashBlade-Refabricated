package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public class LivingKnockBackEvent extends LivingEvent implements ICancellableEvent {
    protected float strength;
    protected double ratioX, ratioZ;
    protected final float originalStrength;
    protected final double originalRatioX, originalRatioZ;

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public LivingKnockBackEvent(LivingEntity target, float strength, double ratioX, double ratioZ) {
        super(target);
        this.strength = this.originalStrength = strength;
        this.ratioX = this.originalRatioX = ratioX;
        this.ratioZ = this.originalRatioZ = ratioZ;
    }

    public float getStrength() {
        return this.strength;
    }

    public double getRatioX() {
        return this.ratioX;
    }

    public double getRatioZ() {
        return this.ratioZ;
    }

    public float getOriginalStrength() {
        return this.originalStrength;
    }

    public double getOriginalRatioX() {
        return this.originalRatioX;
    }

    public double getOriginalRatioZ() {
        return this.originalRatioZ;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public void setRatioX(double ratioX) {
        this.ratioX = ratioX;
    }

    public void setRatioZ(double ratioZ) {
        this.ratioZ = ratioZ;
    }

    public interface Callback {
        void post(LivingKnockBackEvent event);
    }
}
