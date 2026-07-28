package mods.flammpfeil.slashblade.data;

import cn.sh1rocu.slashblade.util.ItemPredicateRegistry;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.advancement.SlashBladeItemPredicate;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipeBuilder;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipeBuilder;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class SlashBladeRecipeProvider extends FabricRecipeProvider {
    public SlashBladeRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new Provider(registries, output);
    }

    public static class Provider extends RecipeProvider {
        protected Provider(HolderLookup.Provider registries, RecipeOutput output) {
            super(registries, output);
        }

        HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);

        @Override
        public void buildRecipes() {
            var consumer = this.output;
            SlashBladeSmithingRecipeBuilder.smithing(
                            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                            SlashBladeIngredient.of(
                                    RequestDefinition.Builder.newInstance()
                                            .name(SlashBladeBuiltInRegistry.RODAI_DIAMOND.identifier())
                                            .build()).toVanilla(),
                            Ingredient.of(items.getOrThrow(ConventionalItemTags.NETHERITE_INGOTS)),
                            RecipeCategory.COMBAT,
                            SlashBladeBuiltInRegistry.RODAI_NETHERITE.identifier())
                    .unlocks(getHasName(Items.NETHERITE_INGOT), has(ConventionalItemTags.NETHERITE_INGOTS))
                    .save(consumer, SlashBlade.prefix("rodai_netherite_smithing"));

            this.shaped(RecipeCategory.COMBAT, SBItems.SLASHBLADE_WOOD).pattern("  L").pattern(" L ")
                    .pattern("B  ").define('B', Items.WOODEN_SWORD).define('L', ItemTags.LOGS)
                    .unlockedBy(getHasName(Items.WOODEN_SWORD), has(Items.WOODEN_SWORD)).save(consumer);
            SlashBladeShapedRecipeBuilder.shaped(SBItems.SLASHBLADE_BAMBOO).pattern("  L").pattern(" L ").pattern("B  ")
                    .define('B', SBItems.SLASHBLADE_WOOD).define('L', SlashBladeItemTags.BAMBOO)
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_WOOD), has(SBItems.SLASHBLADE_WOOD)).save(consumer);
            SlashBladeShapedRecipeBuilder.shaped(SBItems.SLASHBLADE_SILVERBAMBOO).pattern(" EI").pattern("SBD")
                    .pattern("PS ").define('B', SBItems.SLASHBLADE_BAMBOO).define('I', ConventionalItemTags.IRON_INGOTS)
                    .define('S', ConventionalItemTags.STRINGS).define('P', Items.PAPER).define('E', Items.EGG)
                    .define('D', ConventionalItemTags.BLACK_DYES)
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_BAMBOO), has(SBItems.SLASHBLADE_BAMBOO)).save(consumer);
            SlashBladeShapedRecipeBuilder.shaped(SBItems.SLASHBLADE_WHITE).pattern("  L").pattern(" L ").pattern("BG ")
                    .define('B', SBItems.SLASHBLADE_WOOD).define('L', SBItems.PROUDSOUL_INGOT)
                    .define('G', ConventionalItemTags.GOLD_INGOTS)
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_WOOD), has(SBItems.SLASHBLADE_WOOD)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.YAMATO.identifier())
                    .pattern("PPP")
                    .pattern("PBP")
                    .pattern("PPP")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.YAMATO.identifier()).addSwordType(SwordType.BROKEN)
                                    .addSwordType(SwordType.SEALED).build()).toVanilla())
                    .define('P', SBItems.PROUDSOUL_SPHERE)
                    .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), inventoryTrigger(ItemPredicate.Builder.item().withComponents(
                            DataComponentMatchers.Builder.components().partial(ItemPredicateRegistry.SLASHBLADE,
                                    new SlashBladeItemPredicate(
                                            RequestDefinition.Builder.newInstance()
                                                    .name(SlashBladeBuiltInRegistry.YAMATO.identifier()).addSwordType(SwordType.BROKEN)
                                                    .addSwordType(SwordType.SEALED).build(),
                                            SBItems.SLASHBLADE.builtInRegistryHolder()

                                    )).build()
                    ))).save(consumer, SlashBlade.prefix("yamato_fix").toString());

            SlashBladeShapedRecipeBuilder.shaped(SBItems.SLASHBLADE).pattern(" EI").pattern("PBD").pattern("SI ")
                    .define('B',
                            SlashBladeIngredient.of(SBItems.SLASHBLADE_WHITE,
                                    RequestDefinition.Builder.newInstance().addSwordType(SwordType.BROKEN).build()).toVanilla())
                    .define('I', ConventionalItemTags.GOLD_INGOTS).define('S', ConventionalItemTags.STRINGS).define('P', ConventionalItemTags.BLUE_DYES)
                    .define('E', ConventionalItemTags.BLAZE_RODS).define('D', ConventionalItemTags.STORAGE_BLOCKS_COAL)
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_WHITE), has(SBItems.SLASHBLADE_WHITE)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.RUBY.identifier()).pattern("DPI").pattern("PB ")
                    .pattern("S  ")
                    .define('B',
                            SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                    RequestDefinition.Builder.newInstance().addSwordType(SwordType.BROKEN).build()).toVanilla())
                    .define('I', SBItems.PROUDSOUL).define('S', ConventionalItemTags.STRINGS).define('P', SBItems.PROUDSOUL_INGOT)
                    .define('D', ConventionalItemTags.RED_DYES)
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                    .save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.FOX_BLACK.identifier()).pattern(" EF")
                    .pattern("BCS").pattern("WQ ").define('W', ConventionalItemTags.WHEAT_CROPS)
                    .define('Q', /*ConventionalItemTags.STORAGE_BLOCKS_QUARTZ*/Items.QUARTZ_BLOCK).define('B', Items.BLAZE_POWDER)
                    .define('S', SBItems.PROUDSOUL_CRYSTAL).define('E', ConventionalItemTags.OBSIDIANS)
                    .define('F', ConventionalItemTags.FEATHERS)
                    .define('C', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                            .name(SlashBladeBuiltInRegistry.RUBY.identifier())
                            .addEnchantment(new EnchantmentDefinition(holder(Enchantments.SMITE), 1)).build()).toVanilla())

                    .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                    .save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.FOX_WHITE.identifier()).pattern(" EF")
                    .pattern("BCS").pattern("WQ ").define('W', ConventionalItemTags.WHEAT_CROPS)
                    .define('Q', /*ConventionalItemTags.STORAGE_BLOCKS_QUARTZ*/Items.QUARTZ_BLOCK).define('B', Items.BLAZE_POWDER)
                    .define('S', SBItems.PROUDSOUL_CRYSTAL).define('E', ConventionalItemTags.OBSIDIANS)
                    .define('F', ConventionalItemTags.FEATHERS)
                    .define('C',
                            SlashBladeIngredient.of(
                                    RequestDefinition.Builder.newInstance().name(SlashBladeBuiltInRegistry.RUBY.identifier())
                                            .addEnchantment(new EnchantmentDefinition(holder(Enchantments.LOOTING), 1))
                                            .build()).toVanilla())

                    .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                    .save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.MURAMASA.identifier()).pattern("SSS")
                    .pattern("SBS").pattern("SSS")
                    .define('B',
                            SlashBladeIngredient
                                    .of(RequestDefinition.Builder.newInstance().proudSoul(10000).refineCount(20).build()).toVanilla())
                    .define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                    .unlockedBy(getHasName(SBItems.SLASHBLADE), inventoryTrigger(ItemPredicate.Builder.item().withComponents(
                            DataComponentMatchers.Builder.components().partial(
                                    ItemPredicateRegistry.SLASHBLADE,
                                    new SlashBladeItemPredicate(
                                            RequestDefinition.Builder.newInstance().build(),
                                            SBItems.SLASHBLADE.builtInRegistryHolder()
                                    )).build()
                    ))).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.TAGAYASAN.identifier()).pattern("SES")
                    .pattern("DBD").pattern("SES")
                    .define('B',
                            SlashBladeIngredient.of(SBItems.SLASHBLADE_WOOD, RequestDefinition.Builder.newInstance()
                                    .addEnchantment(new EnchantmentDefinition(holder(Enchantments.UNBREAKING), 1))
                                    .proudSoul(1000).refineCount(10).build()).toVanilla())
                    .define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE)).define('E', Ingredient.of(Items.ENDER_EYE))
                    .define('D', Ingredient.of(Items.ENDER_PEARL))
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_WOOD), has(SBItems.SLASHBLADE_WOOD)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.AGITO.identifier()).pattern(" S ").pattern("SBS")
                    .pattern(" S ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.AGITO_RUST.identifier()).killCount(100).build()).toVanilla())
                    .define('S', Ingredient.of(SBItems.PROUDSOUL))
                    .unlockedBy(getHasName(SBItems.PROUDSOUL), has(SBItems.PROUDSOUL)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.OROTIAGITO_SEALED.identifier()).pattern(" S ")
                    .pattern("SBS").pattern(" S ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.OROTIAGITO_RUST.identifier()).killCount(100).build()).toVanilla())
                    .define('S', Ingredient.of(SBItems.PROUDSOUL))
                    .unlockedBy(getHasName(SBItems.PROUDSOUL), has(SBItems.PROUDSOUL)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.OROTIAGITO.identifier()).pattern("PSP")
                    .pattern("SBS").pattern("PSP")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.OROTIAGITO_SEALED.identifier()).killCount(1000)
                                    .proudSoul(1000).refineCount(10).build()).toVanilla())
                    .define('P', Ingredient.of(SBItems.PROUDSOUL)).define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                    .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), has(SBItems.PROUDSOUL_SPHERE)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.DOUTANUKI.identifier()).pattern("  P")
                    .pattern(" B ").pattern("P  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.SABIGATANA.identifier()).killCount(100).proudSoul(1000)
                                    .refineCount(10).build()).toVanilla())
                    .define('P', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                    .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), has(SBItems.PROUDSOUL_SPHERE)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.SABIGATANA.identifier()).pattern("  P")
                    .pattern(" P ").pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeBuiltInRegistry.SABIGATANA.identifier()).addSwordType(SwordType.BROKEN)
                                    .addSwordType(SwordType.SEALED).build()).toVanilla())
                    .define('P', Ingredient.of(SBItems.PROUDSOUL_INGOT))
                    .unlockedBy(getHasName(SBItems.PROUDSOUL_INGOT), has(SBItems.PROUDSOUL_INGOT)).save(consumer);

            SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.TUKUMO.identifier()).pattern("ESD").pattern("RBL")
                    .pattern("ISG").define('D', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                    .define('L', ConventionalItemTags.STORAGE_BLOCKS_LAPIS).define('G', ConventionalItemTags.STORAGE_BLOCKS_GOLD)
                    .define('I', ConventionalItemTags.STORAGE_BLOCKS_IRON).define('R', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
                    .define('E', ConventionalItemTags.STORAGE_BLOCKS_EMERALD)
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .addEnchantment(
                                            new EnchantmentDefinition(holder(Enchantments.FIRE_ASPECT), 1))
                                    .build()).toVanilla())
                    .define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                    .unlockedBy(getHasName(SBItems.SLASHBLADE), inventoryTrigger(ItemPredicate.Builder.item().withComponents(
                            DataComponentMatchers.Builder.components().partial(
                                    ItemPredicateRegistry.SLASHBLADE,
                                    new SlashBladeItemPredicate(
                                            RequestDefinition.Builder.newInstance().build(),
                                            SBItems.SLASHBLADE.builtInRegistryHolder()
                                    )).build()
                    ))).save(consumer);

            rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_WOODEN.identifier(), Items.WOODEN_SWORD, consumer);
            rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_STONE.identifier(), Items.STONE_SWORD, consumer);
            rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_IRON.identifier(), Items.IRON_SWORD, consumer);
            rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_GOLDEN.identifier(), Items.GOLDEN_SWORD, consumer);
            rodaiAdvRecipe(SlashBladeBuiltInRegistry.RODAI_DIAMOND.identifier(), Items.DIAMOND_SWORD, consumer);
            rodaiAdvRecipe(SlashBladeBuiltInRegistry.RODAI_NETHERITE.identifier(), Items.NETHERITE_SWORD, consumer);
        }

        private void rodaiRecipe(Identifier rodai, ItemLike sword, RecipeOutput consumer) {
            SlashBladeShapedRecipeBuilder.shaped(rodai).pattern("  P").pattern(" B ").pattern("WS ").define('B',
                            SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                    RequestDefinition.Builder.newInstance().killCount(100).addSwordType(SwordType.BROKEN).build()).toVanilla())
                    .define('W', Ingredient.of(sword)).define('S', ConventionalItemTags.STRINGS)
                    .define('P', Ingredient.of(SBItems.PROUDSOUL_CRYSTAL))
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                    .save(consumer);
        }

        private void rodaiAdvRecipe(Identifier rodai, ItemLike sword, RecipeOutput consumer) {
            SlashBladeShapedRecipeBuilder.shaped(rodai).pattern("  P").pattern(" B ").pattern("WS ").define('B',
                            SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                    RequestDefinition.Builder.newInstance().killCount(100).addSwordType(SwordType.BROKEN).build()).toVanilla())
                    .define('W', Ingredient.of(sword)).define('S', Ingredient.of(items.getOrThrow(ConventionalItemTags.STRINGS)))
                    .define('P', Ingredient.of(SBItems.PROUDSOUL_TRAPEZOHEDRON))
                    .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                    .save(consumer);
        }

        private Holder<Enchantment> holder(ResourceKey<Enchantment> key) {
            return this.registries.lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(key);
        }
    }

    @Override
    public String getName() {
        return "SlashBlade recipes";
    }
}
