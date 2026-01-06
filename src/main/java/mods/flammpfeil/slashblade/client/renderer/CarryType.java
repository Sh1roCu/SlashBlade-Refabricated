package mods.flammpfeil.slashblade.client.renderer;

import com.mojang.serialization.Codec;

import java.util.Locale;

public enum CarryType {
    NONE, DEFAULT, PSO2, NINJA, KATANA, RNINJA;

    public static final Codec<CarryType> CODEC = Codec.STRING.xmap(string -> CarryType.valueOf(string.toUpperCase(Locale.ENGLISH)),
            instance -> instance.name().toLowerCase(Locale.ENGLISH));
}
