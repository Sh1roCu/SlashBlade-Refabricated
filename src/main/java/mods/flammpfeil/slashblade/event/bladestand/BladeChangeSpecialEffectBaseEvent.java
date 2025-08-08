package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class BladeChangeSpecialEffectBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
    private ResourceLocation SEKey;
    private int shrinkCount = 0;
    private final BladeStandAttackBaseEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onChangeSpecialEffect(event);
        }
    });

    public BladeChangeSpecialEffectBaseEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                             BladeStandAttackBaseEvent originalEvent) {
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

    public @Nullable BladeStandAttackBaseEvent getOriginalEvent() {
        return originalEvent;
    }

    public interface Callback {
        void onChangeSpecialEffect(BladeChangeSpecialEffectBaseEvent event);
    }
}
