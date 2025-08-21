package mods.flammpfeil.slashblade.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SlashBladeSmithingRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final ResourceLocation result;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    private final RecipeSerializer<?> type;

    public SlashBladeSmithingRecipeBuilder(RecipeSerializer<?> serializer, Ingredient template, Ingredient base,
                                           Ingredient addition, RecipeCategory category, ResourceLocation result) {
        this.category = category;
        this.type = serializer;
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

    public SlashBladeSmithingRecipeBuilder unlocks(String name, CriterionTriggerInstance trigger) {
        this.advancement.addCriterion(name, trigger);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, String name) {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        this.ensureValid(id);
        this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumer.accept(new SlashBladeSmithingRecipeBuilder.Result(id, this.type, this.template, this.base,
                this.addition, this.result, this.advancement,
                id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base,
                                Ingredient addition, ResourceLocation result, Advancement.Builder advancement,
                                ResourceLocation advancementId)
            implements FinishedRecipe {
        public void serializeRecipeData(JsonObject p_266713_) {
            p_266713_.add("template", this.template.toJson());
            p_266713_.add("base", this.base.toJson());
            p_266713_.add("addition", this.addition.toJson());
            p_266713_.addProperty("blade", this.result.toString());
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return this.type;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }

}