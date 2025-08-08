package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.event.drop.EntityDropEntry;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class RegistryHandler {

    public static void onDatapackRegister() {
        DynamicRegistries.registerSynced(SlashBladeDefinition.REGISTRY_KEY, SlashBladeDefinition.CODEC,
                SlashBladeDefinition.CODEC);

        DynamicRegistries.registerSynced(EntityDropEntry.REGISTRY_KEY, EntityDropEntry.CODEC, EntityDropEntry.CODEC);
    }

    // register CustomIngredientSerializer
    public static void registerSerializers() {
        CustomIngredientSerializer.register(SlashBladeIngredient.Serializer.INSTANCE);
    }
}