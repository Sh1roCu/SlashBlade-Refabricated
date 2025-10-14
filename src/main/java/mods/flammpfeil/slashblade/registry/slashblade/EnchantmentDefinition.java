package mods.flammpfeil.slashblade.registry.slashblade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;

public class EnchantmentDefinition {
    public static final Codec<EnchantmentDefinition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantmentDefinition::getEnchantmentID),
                    Codec.INT.optionalFieldOf("lvl", 1).forGetter(EnchantmentDefinition::getEnchantmentLevel))
            .apply(instance, EnchantmentDefinition::new));

    public static StreamCodec<RegistryFriendlyByteBuf, EnchantmentDefinition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            EnchantmentDefinition::getEnchantmentID,
            ByteBufCodecs.INT,
            EnchantmentDefinition::getEnchantmentLevel,
            EnchantmentDefinition::new
    );

    private final ResourceLocation id;
    private final int lvl;

    public EnchantmentDefinition(ResourceLocation enchantment, int level) {
        this.id = enchantment;
        this.lvl = level;
    }

    public ResourceLocation getEnchantmentID() {
        return id;
    }

    public int getEnchantmentLevel() {
        return lvl;
    }
}