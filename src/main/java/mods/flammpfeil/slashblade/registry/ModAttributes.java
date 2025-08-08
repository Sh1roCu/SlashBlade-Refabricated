package mods.flammpfeil.slashblade.registry;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    public static final Attribute SLASHBLADE_DAMAGE = register("slashblade_damage",
            new RangedAttribute("attribute.name.generic.slashblade_damage", 1.0d, 0.0d, 512.0d).setSyncable(true));


    public static Attribute getSlashBladeDamage() {
        return SLASHBLADE_DAMAGE;
    }

    private static Attribute register(String name, Attribute attribute) {
        return Registry.register(BuiltInRegistries.ATTRIBUTE, SlashBlade.prefix(name), attribute);
    }

    public static void init() {

    }
}
