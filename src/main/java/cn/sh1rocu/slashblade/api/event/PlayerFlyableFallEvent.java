package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class PlayerFlyableFallEvent extends PlayerEvent {
    private float distance;
    private float multiplier;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onPlayerFlyableFall(event);
        }
    });

    public PlayerFlyableFallEvent(Player player, float distance, float multiplier) {
        super(player);
        this.distance = distance;
        this.multiplier = multiplier;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public interface Callback {
        void onPlayerFlyableFall(PlayerFlyableFallEvent event);
    }
}