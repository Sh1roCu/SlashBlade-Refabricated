package mods.flammpfeil.slashblade.slasharts;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.function.Function;

public class SlashArts {
    public static final ResourceKey<Registry<SlashArts>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(SlashBlade.prefix("slash_arts"));

    public static Identifier getRegistryKey(SlashArts state) {
        return SlashArtsRegistry.SLASH_ARTS.getKey(state);
    }

    public static final int ChargeTicks = 9;
    public static final int ChargeJustTicks = 3;
    public static final int ChargeJustTicksMax = 5;

    public static int getJustReceptionSpan(LivingEntity user) {
        var holder = user.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED);
        return Math.min(ChargeJustTicksMax,
                ChargeJustTicks + EnchantmentHelper.getEnchantmentLevel(holder, user));
    }

    public enum ArtsType {
        Fail, Success, Jackpot, Super
    }

    private final Function<LivingEntity, Identifier> comboState;
    private Function<LivingEntity, Identifier> comboStateJust;
    private Function<LivingEntity, Identifier> comboStateSuper;

    public Identifier doArts(ArtsType type, LivingEntity user) {
        switch (type) {
            case Jackpot:
                return getComboStateJust(user);
            case Success:
                return getComboState(user);
            case Super:
                return getComboStateSuper().apply(user);
            default:
                break;
        }
        return ComboStateRegistry.getId(ComboStateRegistry.NONE);
    }

    private int costSoul = 20;

    public SlashArts(Function<LivingEntity, Identifier> state) {
        this.comboState = state;
        this.comboStateJust = state;
        this.setComboStateSuper((entity) -> ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_END));
    }

    public Identifier getComboState(LivingEntity user) {
        return this.comboState.apply(user);
    }

    public Identifier getComboStateJust(LivingEntity user) {
        return this.comboStateJust.apply(user);
    }

    public SlashArts setComboStateJust(Function<LivingEntity, Identifier> state) {
        this.comboStateJust = state;
        return this;
    }

    public Function<LivingEntity, Identifier> getComboStateSuper() {
        return comboStateSuper;
    }

    public SlashArts setComboStateSuper(Function<LivingEntity, Identifier> comboStateSuper) {
        this.comboStateSuper = comboStateSuper;
        return this;
    }

    public int getProudSoulCost() {
        return costSoul;
    }

    public SlashArts setProudSoulCost(int costSoul) {
        this.costSoul = costSoul;
        return this;
    }

    public Component getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    public String toString() {
        return SlashArtsRegistry.SLASH_ARTS.getKey(this).toString();
    }

    private String descriptionId;

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("slash_art", SlashArtsRegistry.SLASH_ARTS.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }


}
