package mods.flammpfeil.slashblade.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

public class SlashBladeSmithingRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final ResourceLocation result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public SlashBladeSmithingRecipeBuilder(RecipeSerializer<?> serializer, Ingredient template, Ingredient base,
                                           Ingredient addition, RecipeCategory category, ResourceLocation result) {
        this.category = category;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static SlashBladeSmithingRecipeBuilder smithing(Ingredient template, Ingredient base,
                                                           Ingredient addition, RecipeCategory category, ResourceLocation result) {
        return new SlashBladeSmithingRecipeBuilder(SlashBladeSmithingRecipe.SERIALIZER, template, base, addition,
                category, result);
    }

    public SlashBladeSmithingRecipeBuilder unlocks(String name, Criterion<?> trigger) {
        this.criteria.put(name, trigger);
        return this;
    }

    public void save(RecipeOutput consumer, String name) {
        this.save(consumer, ResourceLocation.parse(name));
    }

    public void save(RecipeOutput consumer, ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder builder = consumer.advancement();
        builder.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        consumer.accept(
                id,
                new SlashBladeSmithingRecipe(this.result, this.template, this.base, this.addition),
                builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}