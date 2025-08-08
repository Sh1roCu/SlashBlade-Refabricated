package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public class LivingDropsEvent extends LivingEvent implements ICancellableEvent {
    private final DamageSource source;
    private final Collection<ItemEntity> drops;
    private final int lootingLevel;
    private final boolean recentlyHit;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onLivingDrops(event);
        }
    });

    public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
        super(entity);
        this.source = source;
        this.drops = drops;
        this.lootingLevel = lootingLevel;
        this.recentlyHit = recentlyHit;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public Collection<ItemEntity> getDrops() {
        return this.drops;
    }

    public int getLootingLevel() {
        return this.lootingLevel;
    }

    public boolean isRecentlyHit() {
        return this.recentlyHit;
    }

    public interface Callback {
        void onLivingDrops(LivingDropsEvent event);
    }
}
