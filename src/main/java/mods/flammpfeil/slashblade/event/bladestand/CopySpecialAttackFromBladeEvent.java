package mods.flammpfeil.slashblade.event.bladestand;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CopySpecialAttackFromBladeEvent extends SlashBladeEvent {
    private final ResourceLocation SAKey;
    private final BladeStandAttackEvent originalEvent;
    private final ItemStack orb;
    private final ItemEntity itemEntity;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onCopySpecialAttack(event);
        }
    });

    public CopySpecialAttackFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                           BladeStandAttackEvent originalEvent,
                                           ItemStack orb, ItemEntity itemEntity) {
        super(blade, state);
        this.SAKey = SAKey;
        this.originalEvent = originalEvent;
        this.orb = orb;
        this.itemEntity = itemEntity;
    }

    public CopySpecialAttackFromBladeEvent(PreCopySpecialAttackFromBladeEvent pe, ItemStack orb,
                                           ItemEntity itemEntity) {
        this(pe.getBlade(), pe.getSlashBladeState(), pe.getSAKey(), pe.getOriginalEvent(), orb, itemEntity);
    }

    public ResourceLocation getSAKey() {
        return SAKey;
    }

    public @Nullable BladeStandAttackEvent getOriginalEvent() {
        return originalEvent;
    }

    public ItemStack getOrb() {
        return orb;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public interface Callback {
        void onCopySpecialAttack(CopySpecialAttackFromBladeEvent event);
    }
}
