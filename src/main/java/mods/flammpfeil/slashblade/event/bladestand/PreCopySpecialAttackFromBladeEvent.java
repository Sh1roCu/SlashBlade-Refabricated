package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PreCopySpecialAttackFromBladeEvent extends SlashBladeEvent implements ICancellableEvent {
    private ResourceLocation SAKey;
    private int shrinkCount = 0;
    private final BladeStandAttackEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onPreCopySpecialAttack(event);
        }
    });

    public PreCopySpecialAttackFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                              BladeStandAttackEvent originalEvent) {
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

    public @Nullable BladeStandAttackEvent getOriginalEvent() {
        return originalEvent;
    }

    public interface Callback {
        void onPreCopySpecialAttack(PreCopySpecialAttackFromBladeEvent event);
    }
}
