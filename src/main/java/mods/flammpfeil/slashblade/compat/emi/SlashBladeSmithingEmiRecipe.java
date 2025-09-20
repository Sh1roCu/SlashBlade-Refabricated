package mods.flammpfeil.slashblade.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlashBladeSmithingEmiRecipe extends EMISimpleRecipe {

    private final SlashBladeSmithingRecipe recipe;

    public SlashBladeSmithingEmiRecipe(SlashBladeSmithingRecipe recipe) {
        super(
                createInputs(recipe),
                createOutputs(recipe),
                recipe.getId()
        );
        this.recipe = recipe;
    }

    private static List<EmiIngredient> createInputs(SlashBladeSmithingRecipe recipe) {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(recipe.getTemplate()));
        inputs.add(EmiIngredient.of(recipe.getBase()));
        inputs.add(EmiIngredient.of(recipe.getAddition()));
        return inputs;
    }

    private static List<EmiStack> createOutputs(SlashBladeSmithingRecipe recipe) {
        List<EmiStack> outputs = new ArrayList<>();

        ItemStack[] additions = recipe.getAddition().getItems();
        if (additions.length == 0) {
            return outputs;
        }

        ItemStack addition = additions[0];
        for (ItemStack template : recipe.getTemplate().getItems()) {
            for (ItemStack base : recipe.getBase().getItems()) {
                Container input = createInput(template, base, addition);
                ItemStack output = assembleResultItem(input, recipe);
                if (!output.isEmpty()) {
                    outputs.add(EmiStack.of(output));
                }
            }
        }

        return outputs;
    }

    private static Container createInput(ItemStack template, ItemStack base, ItemStack addition) {
        Container container = new SimpleContainer(3);
        container.setItem(0, template);
        container.setItem(1, base);
        container.setItem(2, addition);
        return container;
    }

    private static ItemStack assembleResultItem(Container input, SlashBladeSmithingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.assemble(input, registryAccess);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMICompat.SLASHBLADE_SMITHING_CATEGORY;
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
        // 模板槽位
        widgets.addSlot(inputs.get(0), 0, 0);
        // 基础物品槽位
        widgets.addSlot(inputs.get(1), 18, 0);
        // 添加物品槽位
        widgets.addSlot(inputs.get(2), 36, 0);
        // 输出槽位
        widgets.addSlot(outputs.get(0), 94, 0).recipeContext(this);
    }

	public SlashBladeSmithingRecipe getRecipe() {
		return recipe;
	}
}