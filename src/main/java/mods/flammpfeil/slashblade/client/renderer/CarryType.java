package mods.flammpfeil.slashblade.client.renderer;

import com.mojang.serialization.Codec;

public enum CarryType {
    NONE, DEFAULT, PSO2, NINJA, KATANA, RNINJA;

    public static final Codec<CarryType> CODEC = Codec.STRING.xmap(string -> CarryType.valueOf(string.toUpperCase()),
            instance -> instance.name().toLowerCase());
}
