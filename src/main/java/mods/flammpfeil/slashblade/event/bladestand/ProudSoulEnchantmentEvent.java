package mods.flammpfeil.slashblade.event.bladestand;

import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;

public class ProudSoulEnchantmentEvent extends SlashBladeEvent implements ICancellableEvent {
    private int totalShrinkCount;
    private float probability;
    private Enchantment enchantment;
    private int enchantLevel;
    private boolean tryNextEnchant;
    private final BladeStandAttackEvent originalEvent;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onProudSoulEnchantment(event);
        }
    });

    public ProudSoulEnchantmentEvent(ItemStack blade, ISlashBladeState state,
                                     Enchantment enchantment, int enchantLevel, boolean tryNextEnchant, float probability,
                                     int totalShrinkCount, BladeStandAttackEvent originalEvent) {
        super(blade, state);
        this.enchantment = enchantment;
        this.enchantLevel = enchantLevel;
        this.tryNextEnchant = tryNextEnchant;
        this.probability = probability;
        this.totalShrinkCount = totalShrinkCount;
        this.originalEvent = originalEvent;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public Enchantment setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
        return enchantment;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public int setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
        return this.enchantLevel;
    }

    public boolean willTryNextEnchant() {
        return tryNextEnchant;
    }

    public boolean setWillTryNextEnchant(boolean tryNextEnchant) {
        this.tryNextEnchant = tryNextEnchant;
        return tryNextEnchant;
    }

    public int getTotalShrinkCount() {
        return totalShrinkCount;
    }

    public int setTotalShrinkCount(int totalShrinkCount) {
        this.totalShrinkCount = totalShrinkCount;
        return this.totalShrinkCount;
    }

    public float getProbability() {
        return probability;
    }

    public float setProbability(float probability) {
        this.probability = probability;
        return this.probability;
    }

    public @Nullable BladeStandAttackEvent getOriginalEvent() {
        return originalEvent;
    }

    public interface Callback {
        void onProudSoulEnchantment(ProudSoulEnchantmentEvent event);
    }
}
