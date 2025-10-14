package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class BladeChangeSpecialEffectEvent extends SlashBladeEvent implements ICancellableEvent {
    private ResourceLocation SEKey;
    private int shrinkCount = 0;
    private final BladeStandAttackEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onChangeSpecialEffect(event);
        }
    });

    public BladeChangeSpecialEffectEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                         BladeStandAttackEvent originalEvent) {
        super(blade, state);
        this.SEKey = SEKey;
        this.originalEvent = originalEvent;
    }

    public ResourceLocation getSEKey() {
        return SEKey;
    }

    public ResourceLocation setSEKey(ResourceLocation SEKey) {
        this.SEKey = SEKey;
        return SEKey;
    }

    public int getShrinkCount() {
        return shrinkCount;
    }

    public int setShrinkCount(int shrinkCount) {
        this.shrinkCount = shrinkCount;
        return this.shrinkCount;
    }

    public @Nullable BladeStandAttackEvent getOriginalEvent() {
        return originalEvent;
    }

    public interface Callback {
        void onChangeSpecialEffect(BladeChangeSpecialEffectEvent event);
    }
}
