package mods.flammpfeil.slashblade.recipe;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeSerializerRegistry {
    public static final RecipeSerializer<?> SLASHBLADE_SHAPED = register("shaped_blade", SlashBladeShapedRecipe.SERIALIZER);

    public static final RecipeSerializer<?> PROUDSOUL_RECIPE = register("proudsoul", ProudsoulShapelessRecipe.SERIALIZER);

    public static final RecipeSerializer<?> SLASHBLADE_SMITHING = register("slashblade_smithing", SlashBladeSmithingRecipe.SERIALIZER);

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, SlashBlade.prefix(name), serializer);
    }

    private static <T extends Recipe<?>> RecipeType<T> register(String name, RecipeType<T> type) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, SlashBlade.prefix(name), type);
    }

    public static void init() {

    }
}
