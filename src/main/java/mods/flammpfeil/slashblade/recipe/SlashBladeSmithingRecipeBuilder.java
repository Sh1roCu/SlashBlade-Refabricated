package mods.flammpfeil.slashblade.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

public class SlashBladeSmithingRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Identifier result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public SlashBladeSmithingRecipeBuilder(RecipeSerializer<?> serializer, Ingredient template, Ingredient base,
                                           Ingredient addition, RecipeCategory category, Identifier result) {
        this.category = category;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static SlashBladeSmithingRecipeBuilder smithing(Ingredient template, Ingredient base,
                                                           Ingredient addition, RecipeCategory category, Identifier result) {
        return new SlashBladeSmithingRecipeBuilder(SlashBladeSmithingRecipe.SERIALIZER, template, base, addition,
                category, result);
    }

    public SlashBladeSmithingRecipeBuilder unlocks(String name, Criterion<?> trigger) {
        this.criteria.put(name, trigger);
        return this;
    }

    public void save(RecipeOutput consumer, String name) {
        this.save(consumer, Identifier.parse(name));
    }

    public void save(RecipeOutput consumer, Identifier id) {
        this.ensureValid(id);
        Advancement.Builder builder = consumer.advancement();
        builder.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key(id)))
                .rewards(AdvancementRewards.Builder.recipe(key(id))).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        consumer.accept(
                key(id),
                new SlashBladeSmithingRecipe(this.result, this.template, this.base, this.addition),
                builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(Identifier id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    private static ResourceKey<Recipe<?>> key(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }
}