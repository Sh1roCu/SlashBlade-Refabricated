package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.event.drop.EntityDropEntry;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RegistryHandler {
    public static Map<Identifier, SlashBladeDefinition> DEFINITIONS = new HashMap<>();

    public static void onDatapackRegister() {
        DynamicRegistries.registerSynced(SlashBladeDefinition.REGISTRY_KEY, SlashBladeDefinition.CODEC,
                SlashBladeDefinition.CODEC);

        DynamicRegistries.registerSynced(EntityDropEntry.REGISTRY_KEY, EntityDropEntry.CODEC, EntityDropEntry.CODEC);

        DynamicRegistrySetupCallback.EVENT.register(view -> {
            view.registerEntryAdded(SlashBladeDefinition.REGISTRY_KEY, (rawId, id, definition) -> {
                DEFINITIONS.put(id, definition);
            });
        });
    }

    // register CustomIngredientSerializer
    public static void registerSerializers() {
        CustomIngredientSerializer.register(SlashBladeIngredient.Serializer.INSTANCE);
    }
}