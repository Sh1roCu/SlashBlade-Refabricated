package mods.flammpfeil.slashblade.data;

import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeEntityDropBuiltInRegistry;
import mods.flammpfeil.slashblade.data.tag.SlashBladeEntityTypeTagProvider;
import mods.flammpfeil.slashblade.event.drop.EntityDropEntry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;

public class DataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        // Recipe
        pack.addProvider(SlashBladeRecipeProvider::new);
        // Tag
        pack.addProvider(SlashBladeEntityTypeTagProvider::new);
        // Dynamic
        pack.addProvider(RegistryDataGenerator::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(SlashBladeDefinition.REGISTRY_KEY, SlashBladeBuiltInRegistry::registerAll);
        registryBuilder.add(EntityDropEntry.REGISTRY_KEY, SlashBladeEntityDropBuiltInRegistry::registerAll);
    }
}
