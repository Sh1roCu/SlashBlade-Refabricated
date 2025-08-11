package mods.flammpfeil.slashblade.item;

import com.mojang.serialization.Codec;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public enum SwordType {
    NONE, EDGEFRAGMENT, BROKEN, ENCHANTED, BEWITCHED, FIERCEREDGE, NOSCABBARD, SEALED,
    ;

    public static final Codec<SwordType> CODEC = Codec.STRING.xmap(string -> SwordType.valueOf(string.toUpperCase()),
            instance -> instance.name().toLowerCase());

    public static EnumSet<SwordType> from(ItemStack itemStackIn) {
        EnumSet<SwordType> types = EnumSet.noneOf(SwordType.class);

        CapabilitySlashBlade.BLADESTATE.maybeGet(itemStackIn).ifPresentOrElse(s -> {
            if (s.isBroken())
                types.add(BROKEN);

            if (s.isSealed())
                types.add(SEALED);

            if (!s.isSealed() && itemStackIn.isEnchanted()
                    && (itemStackIn.hasCustomHoverName() || s.isDefaultBewitched()))
                types.add(BEWITCHED);

            if (s.getKillCount() >= 1000)
                types.add(FIERCEREDGE);

        }, () -> {
            types.add(NOSCABBARD);
            types.add(EDGEFRAGMENT);
        });

        if (itemStackIn.isEnchanted())
            types.add(ENCHANTED);

        if (itemStackIn.getItem() instanceof ItemSlashBladeDetune) {
            types.remove(SwordType.ENCHANTED);
            types.remove(SwordType.BEWITCHED);
        }

        return types;
    }
}
