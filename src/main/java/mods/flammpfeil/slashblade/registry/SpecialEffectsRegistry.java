package mods.flammpfeil.slashblade.registry;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.WitherEdge;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;

public class SpecialEffectsRegistry {
    public static void init() {

    }

    public static final Registry<SpecialEffect> SPECIAL_EFFECT = FabricRegistryBuilder
            .createSimple(SpecialEffect.REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final SpecialEffect WITHER_EDGE = Registry.register(SPECIAL_EFFECT, SlashBlade.prefix("wither_edge"), new WitherEdge());
}
