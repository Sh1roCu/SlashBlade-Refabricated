package mods.flammpfeil.slashblade.recipe;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

public class SlashBladeShapedRecipe extends ShapedRecipe {
    public static final RecipeSerializer<SlashBladeShapedRecipe> SERIALIZER = new SlashBladeShapedRecipeSerializer();

    final ShapedRecipePattern pattern;
    final ItemStack result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;
    final ResourceLocation outputBlade;

    public SlashBladeShapedRecipe(String group, CraftingBookCategory craftingBookCategory, ShapedRecipePattern shapedRecipePattern, ItemStack itemStack, boolean bl, ResourceLocation blade) {
        super(group, craftingBookCategory, shapedRecipePattern, itemStack, bl);
        this.group = group;
        this.category = craftingBookCategory;
        this.pattern = shapedRecipePattern;
        this.result = itemStack;
        this.showNotification = bl;
        this.outputBlade = blade;
    }

    public String getGroup() {
        return this.group;
    }

    public CraftingBookCategory category() {
        return this.category;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    public boolean showNotification() {
        return this.showNotification;
    }

    public static ItemStack getResultBlade(ResourceLocation outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.get(outputBlade)
                : SBItems.SLASHBLADE;

        return bladeItem.getDefaultInstance();
    }

    public ResourceLocation getOutputBlade() {
        return outputBlade;
    }

    private ResourceKey<SlashBladeDefinition> getOutputBladeKey() {
        return ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBlade);
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        ItemStack result = SlashBladeShapedRecipe.getResultBlade(this.getOutputBlade());

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(getOutputBlade())) {
            result = provider.lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY).getOrThrow(getOutputBladeKey())
                    .value().getBlade();
        }

        return result;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container, HolderLookup.@NotNull Provider provider) {
        var result = this.getResultItem(provider);
        if (!(result.getItem() instanceof ItemSlashBlade)) {
            result = new ItemStack(SBItems.SLASHBLADE);
        }

        var resultState = CapabilitySlashBlade.getBladeState(result).orElseThrow(NullPointerException::new);
        boolean sumRefine = SlashBladeConfig.DO_CRAFTING_SUM_REFINE.get();
        int proudSoul = resultState.getProudSoulCount();
        int killCount = resultState.getKillCount();
        int refine = resultState.getRefine();
        for (var stack : container.items()) {
            if (!(stack.getItem() instanceof ItemSlashBlade))
                continue;
            var ingredientState = CapabilitySlashBlade.getBladeState(stack).orElseThrow(NullPointerException::new);

            proudSoul += ingredientState.getProudSoulCount();
            killCount += ingredientState.getKillCount();
            if (sumRefine) {
                refine += ingredientState.getRefine();
            } else {
                refine = Math.max(refine, ingredientState.getRefine());
            }
            updateEnchantment(result, stack);
        }

        resultState.setProudSoulCount(proudSoul);
        resultState.setKillCount(killCount);
        resultState.setRefine(refine);

        return result;
    }

    private void updateEnchantment(ItemStack result, ItemStack ingredient) {
        var newItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(result);
        var oldItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(ingredient);
        for (Holder<Enchantment> enchantIndex : oldItemEnchants.keySet()) {
            Enchantment enchantment = enchantIndex.value();

            int destLevel = newItemEnchants.getLevel(enchantIndex);
            int srcLevel = oldItemEnchants.getLevel(enchantIndex);

            srcLevel = Math.max(srcLevel, destLevel);
            srcLevel = Math.min(srcLevel, enchantment.getMaxLevel());

            boolean canApplyFlag = result.canBeEnchantedWith(enchantIndex, EnchantingContext.ACCEPTABLE);
            if (canApplyFlag) {
                for (Holder<Enchantment> curEnchantIndex : newItemEnchants.keySet()) {
                    if (curEnchantIndex.value() != enchantment
                            && !Enchantment.areCompatible(enchantIndex, curEnchantIndex)) {
                        canApplyFlag = false;
                        break;
                    }
                }
                if (canApplyFlag) {
                    int finalSrcLevel = srcLevel;
                    EnchantmentHelper.updateEnchantments(result, mutable -> mutable.set(enchantIndex, finalSrcLevel));
                }
            }
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
