package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class SBStatTypes {
    public static void init() {

    }

    public static Identifier SWORD_SUMMONED = registerCustomStat("sword_summoned");

    private static Identifier registerCustomStat(String name) {
        Identifier resourcelocation = SlashBlade.prefix(name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, name, resourcelocation);
        Stats.CUSTOM.get(resourcelocation, StatFormatter.DEFAULT);
        return resourcelocation;
    }
}
