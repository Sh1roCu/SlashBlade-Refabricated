package mods.flammpfeil.slashblade.data;

import mods.flammpfeil.slashblade.event.drop.EntityDropEntry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class RegistryDataGenerator extends FabricDynamicRegistryProvider {
    public RegistryDataGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        entries.addAll(provider.lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY));
        entries.addAll(provider.lookupOrThrow(EntityDropEntry.REGISTRY_KEY));
    }

    @Override
    public String getName() {
        return "SlashBlade: Refabricated Registries";
    }
}