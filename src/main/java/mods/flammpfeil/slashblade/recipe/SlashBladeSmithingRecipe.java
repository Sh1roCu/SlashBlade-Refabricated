package mods.flammpfeil.slashblade.recipe;

import com.google.gson.JsonObject;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class SlashBladeSmithingRecipe implements SmithingRecipe {
    public static final RecipeSerializer<SlashBladeSmithingRecipe> SERIALIZER = new SlashBladeSmithingRecipe.Serializer();
    private final ResourceLocation outputBlade;
    private final ResourceLocation id;

    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;

    public SlashBladeSmithingRecipe(ResourceLocation id, ResourceLocation outputBlade, Ingredient template, Ingredient base, Ingredient addition) {
        super();
        this.id = id;
        this.outputBlade = outputBlade;
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    public SlashBladeSmithingRecipe(ResourceLocation outputBlade, Ingredient template, Ingredient base, Ingredient addition) {
        this(outputBlade, outputBlade, template, base, addition);
    }

    public Ingredient getTemplate() {
        return template;
    }

    public Ingredient getBase() {
        return base;
    }

    public Ingredient getAddition() {
        return addition;
    }

    private static ItemStack getResultBlade(ResourceLocation outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.get(outputBlade)
                : SBItems.slashblade;

        return bladeItem.getDefaultInstance();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        ItemStack result = SlashBladeSmithingRecipe.getResultBlade(this.getOutputBlade());

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(getOutputBlade())) {
            result = access.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY).get(getOutputBlade())
                    .getBlade();
        }

        return result;
    }

    @Override
    public boolean matches(Container container, @NotNull Level level) {
        return this.template.test(container.getItem(0)) && this.base.test(container.getItem(1)) && this.addition.test(container.getItem(2));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
        var result = this.getResultItem(access);
        if (!(result.getItem() instanceof ItemSlashBlade)) {
            result = new ItemStack(SBItems.slashblade);
        }

        var resultState = CapabilitySlashBlade.BLADESTATE.maybeGet(result).orElseThrow(NullPointerException::new);
        var stack = container.getItem(1);
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(stack).isEmpty())
            return ItemStack.EMPTY;
        var ingredientState = CapabilitySlashBlade.BLADESTATE.maybeGet(stack).orElseThrow(NullPointerException::new);

        resultState.setProudSoulCount(resultState.getProudSoulCount() + ingredientState.getProudSoulCount());
        resultState.setKillCount(resultState.getKillCount() + ingredientState.getKillCount());
        resultState.setRefine(resultState.getRefine() + ingredientState.getRefine());
        updateEnchantment(result, stack);

        return result;
    }


    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SlashBladeSmithingRecipe.SERIALIZER;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(SlashBladeSmithingRecipe::hasNoElements);
    }

    @Override
    public boolean isTemplateIngredient(@NotNull ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(@NotNull ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(@NotNull ItemStack stack) {
        return this.addition.test(stack);
    }

    public ResourceLocation getOutputBlade() {
        return outputBlade;
    }

    private void updateEnchantment(ItemStack result, ItemStack ingredient) {
        var newItemEnchants = EnchantmentHelper.deserializeEnchantments(result.getEnchantmentTags());
        var oldItemEnchants = EnchantmentHelper.deserializeEnchantments(ingredient.getEnchantmentTags());
        for (Enchantment enchantIndex : oldItemEnchants.keySet()) {
            Enchantment enchantment = enchantIndex;

            int destLevel = newItemEnchants.containsKey(enchantIndex) ? newItemEnchants.get(enchantIndex) : 0;
            int srcLevel = oldItemEnchants.get(enchantIndex);

            srcLevel = Math.max(srcLevel, destLevel);
            srcLevel = Math.min(srcLevel, enchantment.getMaxLevel());

            // boolean canApplyFlag = enchantment.canApplyAtEnchantingTable(result);
            boolean canApplyFlag = enchantment.canEnchant(result);
            if (canApplyFlag) {
                for (Enchantment curEnchantIndex : newItemEnchants.keySet()) {
                    if (curEnchantIndex != enchantIndex
                            && !enchantment.isCompatibleWith(curEnchantIndex) /* canApplyTogether */) {
                        canApplyFlag = false;
                        break;
                    }
                }
                if (canApplyFlag)
                    newItemEnchants.put(enchantIndex, Integer.valueOf(srcLevel));
            }
        }
        EnchantmentHelper.setEnchantments(newItemEnchants, result);
    }

    public static class Serializer implements RecipeSerializer<SlashBladeSmithingRecipe> {
        public @NotNull SlashBladeSmithingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(json, "template"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(json, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(json, "addition"));
            ResourceLocation output = new ResourceLocation(GsonHelper.getAsString(json, "blade"));
            return new SlashBladeSmithingRecipe(id, output, ingredient, ingredient1, ingredient2);
        }

        public @NotNull SlashBladeSmithingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            Ingredient ingredient2 = Ingredient.fromNetwork(buffer);
            ResourceLocation blade = buffer.readResourceLocation();
            return new SlashBladeSmithingRecipe(id, blade, ingredient, ingredient1, ingredient2);
        }

        public void toNetwork(@NotNull FriendlyByteBuf buffer, SlashBladeSmithingRecipe recipe) {
            recipe.template.toNetwork(buffer);
            recipe.base.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
            buffer.writeResourceLocation(recipe.outputBlade);
        }
    }

    private static boolean hasNoElements(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return true;
        if (items.length == 1) {
            //If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
            ItemStack item = items[0];
            return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
        }
        return false;
    }
}