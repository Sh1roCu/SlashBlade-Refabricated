package mods.flammpfeil.slashblade.recipe;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSerializerRegistry {
    public static final RecipeSerializer<?> SLASHBLADE_SHAPED = register("shaped_blade", SlashBladeShapedRecipe.SERIALIZER);

    public static final RecipeSerializer<?> PROUDSOUL_RECIPE = register("proudsoul", ProudsoulShapelessRecipe.SERIALIZER);

    private static RecipeSerializer<?> register(String name, RecipeSerializer<?> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, SlashBlade.prefix(name), serializer);
    }

    public static void init() {

    }
}
