package mods.flammpfeil.slashblade.event;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

import java.util.ArrayList;

public class Scheduler {
    public static final TimerQueue.Packed<LivingEntity> SB_CALLBACKS = new TimerQueue.Packed<>(new ArrayList<>());

    private final TimerQueue<LivingEntity> queue = new TimerQueue<>(SB_CALLBACKS);

    public Scheduler() {
    }

    public void onTick(LivingEntity entity) {
        queue.tick(entity, entity.level().getGameTime());
    }

    public void schedule(String key, long time, Callback callback) {
        queue.schedule(key, time, callback);
    }

    public interface Callback extends TimerCallback<LivingEntity> {
        @Override
        default MapCodec<? extends TimerCallback<LivingEntity>> codec() {
            return MapCodec.unit(this);
        }
    }
}
