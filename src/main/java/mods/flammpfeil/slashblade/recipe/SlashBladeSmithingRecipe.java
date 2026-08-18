package mods.flammpfeil.slashblade.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record SlashBladeSmithingRecipe(ResourceLocation outputBlade, Ingredient template, Ingredient base,
                                       Ingredient addition) implements SmithingRecipe {
    public static final MapCodec<SlashBladeSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("blade").forGetter(SlashBladeSmithingRecipe::outputBlade),
            Ingredient.CODEC.fieldOf("template").forGetter(SlashBladeSmithingRecipe::template),
            Ingredient.CODEC.fieldOf("base").forGetter(SlashBladeSmithingRecipe::base),
            Ingredient.CODEC.fieldOf("addition").forGetter(SlashBladeSmithingRecipe::addition)
    ).apply(instance, SlashBladeSmithingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlashBladeSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            SlashBladeSmithingRecipe::outputBlade,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::template,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC,
            SlashBladeSmithingRecipe::addition,
            SlashBladeSmithingRecipe::new
    );

    public static final RecipeSerializer<SlashBladeSmithingRecipe> SERIALIZER = new SlashBladeSmithingRecipe.Serializer();


    private static ItemStack getResultBlade(ResourceLocation outputBlade) {
        Item bladeItem = BuiltInRegistries.ITEM.containsKey(outputBlade) ? BuiltInRegistries.ITEM.get(outputBlade)
                : SBItems.SLASHBLADE;

        return bladeItem.getDefaultInstance();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        ItemStack result = SlashBladeSmithingRecipe.getResultBlade(outputBlade);

        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(outputBlade)) {
            result = provider.lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY).getOrThrow(ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBlade))
                    .value().getBlade();
        }

        return result;
    }

    @Override
    public boolean matches(SmithingRecipeInput container, @NotNull Level level) {
        return this.template.test(container.getItem(0)) && this.base.test(container.getItem(1)) && this.addition.test(container.getItem(2));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SmithingRecipeInput container, HolderLookup.@NotNull Provider provider) {
        var result = this.getResultItem(provider);
        if (!(result.getItem() instanceof ItemSlashBlade)) {
            result = new ItemStack(SBItems.SLASHBLADE);
        }

        var resultState = CapabilitySlashBlade.getBladeState(result).orElseThrow(NullPointerException::new);
        var stack = container.getItem(1);
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return ItemStack.EMPTY;
        var ingredientState = CapabilitySlashBlade.getBladeState(stack).orElseThrow(NullPointerException::new);

        resultState.setProudSoulCount(resultState.getProudSoulCount() + ingredientState.getProudSoulCount());
        resultState.setKillCount(
                SlashBladeConfig.DO_CRAFTING_SUM_REFINE.get() ?
                        Math.max(resultState.getKillCount(), ingredientState.getKillCount()) :
                        resultState.getKillCount() + ingredientState.getKillCount()
        );
        resultState.setRefine(resultState.getRefine() + ingredientState.getRefine());
        updateEnchantment(result, stack);

        return result;
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

    private void updateEnchantment(ItemStack result, ItemStack ingredient) {
        var newItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(result);
        var oldItemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(ingredient);
        for (Holder<Enchantment> enchantIndex : oldItemEnchants.keySet()) {
            Enchantment enchantment = enchantIndex.value();

            int destLevel = Math.max(newItemEnchants.getLevel(enchantIndex), 0);
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

    public static class Serializer implements RecipeSerializer<SlashBladeSmithingRecipe> {
        @Override
        public @NotNull MapCodec<SlashBladeSmithingRecipe> codec() {
            return SlashBladeSmithingRecipe.CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SlashBladeSmithingRecipe> streamCodec() {
            return SlashBladeSmithingRecipe.STREAM_CODEC;
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