package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PreCopySpecialEffectFromBladeEvent extends SlashBladeEvent implements ICancellableEvent {
    private ResourceLocation SEKey;
    private int shrinkCount = 0;
    private boolean isRemovable;
    private boolean isCopiable;
    private final BladeStandAttackEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onPreCopySpecialEffect(event);
        }
    });

    public PreCopySpecialEffectFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                              BladeStandAttackEvent originalEvent, boolean isRemovable, boolean isCopiable) {
        super(blade, state);
        this.SEKey = SEKey;
        this.isRemovable = isRemovable;
        this.isCopiable = isCopiable;
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

    public boolean isRemovable() {
        return isRemovable;
    }

    public boolean setRemovable(boolean isRemovable) {
        this.isRemovable = isRemovable;
        return isRemovable;
    }

    public boolean isCopiable() {
        return isCopiable;
    }

    public boolean setCopiable(boolean isCopiable) {
        this.isCopiable = isCopiable;
        return isCopiable;
    }

    public interface Callback {
        void onPreCopySpecialEffect(PreCopySpecialEffectFromBladeEvent event);
    }
}
