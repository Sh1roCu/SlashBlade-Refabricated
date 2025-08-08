package mods.flammpfeil.slashblade.event.bladestand;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CopySpecialEffectFromBladeBaseEvent extends SlashBladeBaseEvent {
    private final ResourceLocation SEKey;
    private final boolean isRemovable;
    private final boolean isCopiable;
    private final BladeStandAttackBaseEvent originalEvent;
    private final ItemStack orb;
    private final ItemEntity itemEntity;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onCopySpecialEffect(event);
        }
    });

    public CopySpecialEffectFromBladeBaseEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                               BladeStandAttackBaseEvent originalEvent, boolean isRemovable, boolean isCopiable,
                                               ItemStack orb, ItemEntity itemEntity) {
        super(blade, state);
        this.SEKey = SEKey;
        this.isRemovable = isRemovable;
        this.isCopiable = isCopiable;
        this.originalEvent = originalEvent;
        this.orb = orb;
        this.itemEntity = itemEntity;
    }

    public CopySpecialEffectFromBladeBaseEvent(PreCopySpecialEffectFromBladeBaseEvent pe, ItemStack orb,
                                               ItemEntity itemEntity) {
        this(pe.getBlade(), pe.getSlashBladeState(), pe.getSEKey(), pe.getOriginalEvent(), pe.isRemovable(),
                pe.isCopiable(), orb, itemEntity);
    }

    public ResourceLocation getSEKey() {
        return SEKey;
    }

    public @Nullable BladeStandAttackBaseEvent getOriginalEvent() {
        return originalEvent;
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public boolean isCopiable() {
        return isCopiable;
    }

    public ItemStack getOrb() {
        return orb;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public interface Callback {
        void onCopySpecialEffect(CopySpecialEffectFromBladeBaseEvent event);
    }
}
