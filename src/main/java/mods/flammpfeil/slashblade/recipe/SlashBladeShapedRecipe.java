package mods.flammpfeil.slashblade.recipe;

import cn.sh1rocu.slashblade.SlashBladeFabric;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.handler.RegistryHandler;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlashBladeShapedRecipe implements CraftingRecipe {
    public static final RecipeSerializer<SlashBladeShapedRecipe> SERIALIZER = new RecipeSerializer<>(
            SlashBladeShapedRecipeSerializer.CODEC, SlashBladeShapedRecipeSerializer.STREAM_CODEC
    );

    final ShapedRecipePattern pattern;
    final ItemStackTemplate result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;
    final Identifier outputBlade;

    public SlashBladeShapedRecipe(String group,
                                  CraftingBookCategory craftingBookCategory,
                                  ShapedRecipePattern shapedRecipePattern, ItemStackTemplate itemStack, boolean showNotification, Identifier blade) {
        this.group = group;
        this.category = craftingBookCategory;
        this.pattern = shapedRecipePattern;
        this.result = itemStack;
        this.showNotification = showNotification;
        this.outputBlade = blade;
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return RecipeSerializerRegistry.SLASHBLADE_SHAPED_TYPE;
    }

    @Override
    public RecipeSerializer<SlashBladeShapedRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public boolean showNotification() {
        return showNotification;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public List<RecipeDisplay> display() {
        ItemStack resultStack = getResultItem();
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.pattern.width(),
                        this.pattern.height(),
                        this.pattern.ingredients().stream().map(e -> e.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(resultStack.typeHolder(), resultStack.getCount(), resultStack.getComponentsPatch())),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    public static ItemStack getResultBlade(Identifier outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.getValue(outputBlade)
                : SBItems.SLASHBLADE;

        return bladeItem.getDefaultInstance();
    }

    public Identifier getOutputBlade() {
        return outputBlade;
    }

    private ResourceKey<SlashBladeDefinition> getOutputBladeKey() {
        return ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBlade);
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    public @NotNull ItemStack getResultItem() {
        ItemStack result = SlashBladeShapedRecipe.getResultBlade(this.getOutputBlade());

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(getOutputBlade())) {
            result = RegistryHandler.DEFINITIONS.get(getOutputBlade()).getBlade();
        }

        return result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container) {
        var result = this.getResultItem();
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
}
