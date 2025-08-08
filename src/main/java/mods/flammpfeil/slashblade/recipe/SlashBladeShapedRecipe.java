package mods.flammpfeil.slashblade.recipe;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SlashBladeShapedRecipe extends ShapedRecipe {

    public static final RecipeSerializer<SlashBladeShapedRecipe> SERIALIZER = new SlashBladeShapedRecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE, SlashBladeShapedRecipe::new);

    private final ResourceLocation outputBlade;

    public SlashBladeShapedRecipe(ShapedRecipe compose, ResourceLocation outputBlade) {
        super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(),
                compose.getIngredients(), getResultBlade(outputBlade));
        this.outputBlade = outputBlade;
    }

    private static ItemStack getResultBlade(ResourceLocation outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.get(outputBlade)
                : SBItems.slashblade;

        return bladeItem.getDefaultInstance();
    }

    public ResourceLocation getOutputBlade() {
        return outputBlade;
    }

    private ResourceKey<SlashBladeDefinition> getOutputBladeKey() {
        return ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBlade);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        ItemStack result = SlashBladeShapedRecipe.getResultBlade(this.getOutputBlade());

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(getOutputBlade())) {
            result = access.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY).getOrThrow(getOutputBladeKey())
                    .getBlade();
        }

        return result;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        var result = this.getResultItem(access);
        if (!(result.getItem() instanceof ItemSlashBlade)) {
            result = new ItemStack(SBItems.slashblade);
        }

        var resultState = CapabilitySlashBlade.BLADESTATE.maybeGet(result).orElseThrow(NullPointerException::new);
        for (var stack : container.getItems()) {
            if (!(stack.getItem() instanceof ItemSlashBlade))
                continue;
            var ingredientState = CapabilitySlashBlade.BLADESTATE.maybeGet(stack).orElseThrow(NullPointerException::new);

            resultState.setProudSoulCount(resultState.getProudSoulCount() + ingredientState.getProudSoulCount());
            resultState.setKillCount(resultState.getKillCount() + ingredientState.getKillCount());
            resultState.setRefine(resultState.getRefine() + ingredientState.getRefine());
            // result.getOrCreateTag().put("bladeState", resultState.serializeNBT());
            updateEnchantment(result, stack);
        }

        return result;
    }

    private void updateEnchantment(ItemStack result, ItemStack ingredient) {
        var newItemEnchants = EnchantmentHelper.getEnchantments(result);
        var oldItemEnchants = EnchantmentHelper.getEnchantments(ingredient);
        for (Enchantment enchantIndex : oldItemEnchants.keySet()) {
            Enchantment enchantment = enchantIndex;

            int destLevel = newItemEnchants.getOrDefault(enchantIndex, 0);
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
