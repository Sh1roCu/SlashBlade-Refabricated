package mods.flammpfeil.slashblade.recipe;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestDefinition {

    public static final Codec<RequestDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("name", SlashBlade.prefix("none"))
                            .forGetter(RequestDefinition::getName),
                    Codec.INT.optionalFieldOf("proud_soul", 0).forGetter(RequestDefinition::getProudSoulCount),
                    Codec.INT.optionalFieldOf("kill", 0).forGetter(RequestDefinition::getKillCount),
                    Codec.INT.optionalFieldOf("refine", 0).forGetter(RequestDefinition::getRefineCount),
                    EnchantmentDefinition.CODEC.listOf().optionalFieldOf("enchantments", Lists.newArrayList())
                            .forGetter(RequestDefinition::getEnchantments),
                    SwordType.CODEC.listOf().optionalFieldOf("sword_type", Lists.newArrayList())
                            .forGetter(RequestDefinition::getDefaultType))
            .apply(instance, RequestDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestDefinition> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            RequestDefinition::getName,
            ByteBufCodecs.INT,
            RequestDefinition::getProudSoulCount,
            ByteBufCodecs.INT,
            RequestDefinition::getKillCount,
            ByteBufCodecs.INT,
            RequestDefinition::getRefineCount,
            EnchantmentDefinition.STREAM_CODEC.apply(ByteBufCodecs.list()),
            RequestDefinition::getEnchantments,
            SwordType.STREAM_CODEC.apply(ByteBufCodecs.list()),
            RequestDefinition::getDefaultType,
            RequestDefinition::new
    );

    private final Identifier name;
    private final int proudSoulCount;
    private final int killCount;
    private final int refineCount;
    private final List<EnchantmentDefinition> enchantments;
    private final List<SwordType> defaultType;

    public RequestDefinition(Identifier name, int proud, int kill, int refine,
                             List<EnchantmentDefinition> enchantments, List<SwordType> defaultType) {
        this.name = name;
        this.proudSoulCount = proud;
        this.killCount = kill;
        this.refineCount = refine;
        this.enchantments = enchantments;
        this.defaultType = defaultType;
    }

    public Identifier getName() {
        return name;
    }

    public int getProudSoulCount() {
        return proudSoulCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getRefineCount() {
        return refineCount;
    }

    public List<EnchantmentDefinition> getEnchantments() {
        return enchantments;
    }

    public List<SwordType> getDefaultType() {
        return defaultType;
    }

    public void initItemStack(ItemStack blade) {
        var state = CapabilitySlashBlade.getBladeState(blade).orElse(new SlashBladeState(blade));
        state.setNonEmpty();
        if (!this.name.equals(SlashBlade.prefix("none")))
            state.setTranslationKey(getTranslationKey());
        state.setProudSoulCount(getProudSoulCount());
        state.setKillCount(getKillCount());
        state.setRefine(getRefineCount());

        this.getEnchantments().forEach(enchantment ->
                blade.enchant(enchantment.getEnchantment(), enchantment.getEnchantmentLevel()));
        this.defaultType.forEach(type -> {
            switch (type) {
                case BEWITCHED -> state.setDefaultBewitched(true);
                case BROKEN -> {
                    blade.setDamageValue(blade.getMaxDamage() - 1);
                    state.setBroken(true);
                }
                case SEALED -> state.setSealed(true);
                default -> {
                }
            }
        });
    }


    public boolean test(ItemStack blade) {
        if (blade == null || blade.isEmpty())
            return false;
        if (CapabilitySlashBlade.getBladeState(blade).isEmpty())
            return false;
        var state = CapabilitySlashBlade.getBladeState(blade).orElseThrow(NullPointerException::new);
        boolean nameCheck;
        if (this.name.equals(SlashBlade.prefix("none"))) {
            nameCheck = state.getTranslationKey().isBlank();
        } else {
            nameCheck = state.getTranslationKey().equals(getTranslationKey());
        }
        boolean proudCheck = state.getProudSoulCount() >= this.getProudSoulCount();
        boolean killCheck = state.getKillCount() >= this.getKillCount();
        boolean refineCheck = state.getRefine() >= this.getRefineCount();

        for (var enchantment : this.getEnchantments()) {
            var ench = enchantment.getEnchantment();
            var requiredLevel = enchantment.getEnchantmentLevel();

            if (EnchantmentHelper.getItemEnchantmentLevel(ench, blade) < requiredLevel) {
                return false;
            }

        }

        boolean types = SwordType.from(blade).containsAll(this.getDefaultType());

        return nameCheck && proudCheck && killCheck && refineCheck && types;
    }

    public String getTranslationKey() {
        return Util.makeDescriptionId("item", this.getName());
    }

    public static class Builder {
        private Identifier name;
        private int proudCount;
        private int killCount;
        private int refineCount;
        private final List<EnchantmentDefinition> enchantments;
        private final List<SwordType> defaultType;

        private Builder() {
            this.name = SlashBlade.prefix("none");
            this.proudCount = 0;
            this.killCount = 0;
            this.refineCount = 0;
            this.enchantments = new ArrayList<>();
            this.defaultType = new ArrayList<>();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder name(Identifier name) {
            this.name = name;
            return this;
        }

        public Builder proudSoul(int proudCount) {
            this.proudCount = proudCount;
            return this;
        }

        public Builder killCount(int killCount) {
            this.killCount = killCount;
            return this;
        }

        public Builder refineCount(int refineCount) {
            this.refineCount = refineCount;
            return this;
        }

        public Builder addEnchantment(EnchantmentDefinition... enchantments) {
            this.enchantments.addAll(Arrays.asList(enchantments));
            return this;
        }

        public Builder addSwordType(SwordType... types) {
            this.defaultType.addAll(Arrays.asList(types));
            return this;
        }

        public RequestDefinition build() {
            return new RequestDefinition(name, proudCount, killCount, refineCount, enchantments, defaultType);
        }
    }
}
