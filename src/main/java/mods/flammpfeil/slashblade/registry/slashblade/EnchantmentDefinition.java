package mods.flammpfeil.slashblade.registry.slashblade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentDefinition {
    public static final Codec<EnchantmentDefinition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Enchantment.CODEC.fieldOf("id").forGetter(EnchantmentDefinition::getEnchantment),
                    Codec.INT.optionalFieldOf("lvl", 1).forGetter(EnchantmentDefinition::getEnchantmentLevel))
            .apply(instance, EnchantmentDefinition::new));

    public static StreamCodec<RegistryFriendlyByteBuf, EnchantmentDefinition> STREAM_CODEC = StreamCodec.composite(
            Enchantment.STREAM_CODEC,
            EnchantmentDefinition::getEnchantment,
            ByteBufCodecs.INT,
            EnchantmentDefinition::getEnchantmentLevel,
            EnchantmentDefinition::new
    );

    private final Holder<Enchantment> holder;
    private final int lvl;

    public EnchantmentDefinition(Holder<Enchantment> enchantment, int level) {
        this.holder = enchantment;
        this.lvl = level;
    }

    public Holder<Enchantment> getEnchantment() {
        return holder;
    }

    public int getEnchantmentLevel() {
        return lvl;
    }
}