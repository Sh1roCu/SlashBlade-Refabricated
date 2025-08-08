package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class BladeChangeSpecialAttackBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
    private ResourceLocation SAKey;
    private int shrinkCount = 0;
    private final BladeStandAttackBaseEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onChangeSpecialAttack(event);
        }
    });

    public BladeChangeSpecialAttackBaseEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                             BladeStandAttackBaseEvent originalEvent) {
        super(blade, state);
        this.SAKey = SAKey;
        this.originalEvent = originalEvent;
    }

    public ResourceLocation getSAKey() {
        return SAKey;
    }

    public ResourceLocation setSAKey(ResourceLocation SAKey) {
        this.SAKey = SAKey;
        return SAKey;
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
        void onChangeSpecialAttack(BladeChangeSpecialAttackBaseEvent event);
    }
}
