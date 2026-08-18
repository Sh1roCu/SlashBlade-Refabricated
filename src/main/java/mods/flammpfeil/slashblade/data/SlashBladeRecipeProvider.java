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
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class SlashBladeRecipeProvider extends FabricRecipeProvider {

    private final CompletableFuture<HolderLookup.Provider> registries;

    @Nullable
    private HolderLookup.Provider provider;

    public SlashBladeRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
        this.registries = registriesFuture;
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        SlashBladeSmithingRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        SlashBladeIngredient.of(
                                RequestDefinition.Builder.newInstance()
                                        .name(SlashBladeBuiltInRegistry.RODAI_DIAMOND.location())
                                        .build()).toVanilla(),
                        Ingredient.of(ConventionalItemTags.NETHERITE_INGOTS),
                        RecipeCategory.COMBAT,
                        SlashBladeBuiltInRegistry.RODAI_NETHERITE.location())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(ConventionalItemTags.NETHERITE_INGOTS))
                .save(consumer, SlashBlade.prefix("rodai_netherite_smithing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, SBItems.SLASHBLADE_WOOD).pattern("  L").pattern(" L ")
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

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.YAMATO.location())
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.YAMATO.location()).addSwordType(SwordType.BROKEN)
                                .addSwordType(SwordType.SEALED).build()).toVanilla())
                .define('P', SBItems.PROUDSOUL_SPHERE)
                .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), inventoryTrigger(ItemPredicate.Builder.item().withSubPredicate(
                        ItemPredicateRegistry.SLASHBLADE,
                        new SlashBladeItemPredicate(
                                RequestDefinition.Builder.newInstance()
                                        .name(SlashBladeBuiltInRegistry.YAMATO.location()).addSwordType(SwordType.BROKEN)
                                        .addSwordType(SwordType.SEALED).build()
                        )

                ))).save(consumer, SlashBlade.prefix("yamato_fix"));

        SlashBladeShapedRecipeBuilder.shaped(SBItems.SLASHBLADE).pattern(" EI").pattern("PBD").pattern("SI ")
                .define('B',
                        SlashBladeIngredient.of(SBItems.SLASHBLADE_WHITE,
                                RequestDefinition.Builder.newInstance().addSwordType(SwordType.BROKEN).build()).toVanilla())
                .define('I', ConventionalItemTags.GOLD_INGOTS).define('S', ConventionalItemTags.STRINGS).define('P', ConventionalItemTags.BLUE_DYES)
                .define('E', ConventionalItemTags.BLAZE_RODS).define('D', ConventionalItemTags.STORAGE_BLOCKS_COAL)
                .unlockedBy(getHasName(SBItems.SLASHBLADE_WHITE), has(SBItems.SLASHBLADE_WHITE)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.RUBY.location()).pattern("DPI").pattern("PB ")
                .pattern("S  ")
                .define('B',
                        SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                RequestDefinition.Builder.newInstance().addSwordType(SwordType.BROKEN).build()).toVanilla())
                .define('I', SBItems.PROUDSOUL).define('S', ConventionalItemTags.STRINGS).define('P', SBItems.PROUDSOUL_INGOT)
                .define('D', ConventionalItemTags.RED_DYES)
                .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                .save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.FOX_BLACK.location()).pattern(" EF")
                .pattern("BCS").pattern("WQ ").define('W', ConventionalItemTags.WHEAT_CROPS)
                .define('Q', /*ConventionalItemTags.STORAGE_BLOCKS_QUARTZ*/Items.QUARTZ_BLOCK).define('B', Items.BLAZE_POWDER)
                .define('S', SBItems.PROUDSOUL_CRYSTAL).define('E', ConventionalItemTags.OBSIDIANS)
                .define('F', ConventionalItemTags.FEATHERS)
                .define('C', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                        .name(SlashBladeBuiltInRegistry.RUBY.location())
                        .addEnchantment(new EnchantmentDefinition(holder(Enchantments.SMITE), 1)).build()).toVanilla())

                .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                .save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.FOX_WHITE.location()).pattern(" EF")
                .pattern("BCS").pattern("WQ ").define('W', ConventionalItemTags.WHEAT_CROPS)
                .define('Q', /*ConventionalItemTags.STORAGE_BLOCKS_QUARTZ*/Items.QUARTZ_BLOCK).define('B', Items.BLAZE_POWDER)
                .define('S', SBItems.PROUDSOUL_CRYSTAL).define('E', ConventionalItemTags.OBSIDIANS)
                .define('F', ConventionalItemTags.FEATHERS)
                .define('C',
                        SlashBladeIngredient.of(
                                RequestDefinition.Builder.newInstance().name(SlashBladeBuiltInRegistry.RUBY.location())
                                        .addEnchantment(new EnchantmentDefinition(holder(Enchantments.LOOTING), 1))
                                        .build()).toVanilla())

                .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                .save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.MURAMASA.location()).pattern("SSS")
                .pattern("SBS").pattern("SSS")
                .define('B',
                        SlashBladeIngredient
                                .of(RequestDefinition.Builder.newInstance().proudSoul(10000).refineCount(20).build()).toVanilla())
                .define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                .unlockedBy(getHasName(SBItems.SLASHBLADE), inventoryTrigger(ItemPredicate.Builder.item().withSubPredicate(
                        ItemPredicateRegistry.SLASHBLADE,
                        new SlashBladeItemPredicate(
                                RequestDefinition.Builder.newInstance().build()
                        )

                ))).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.TAGAYASAN.location()).pattern("SES")
                .pattern("DBD").pattern("SES")
                .define('B',
                        SlashBladeIngredient.of(SBItems.SLASHBLADE_WOOD, RequestDefinition.Builder.newInstance()
                                .addEnchantment(new EnchantmentDefinition(holder(Enchantments.UNBREAKING), 1))
                                .proudSoul(1000).refineCount(10).build()).toVanilla())
                .define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE)).define('E', Ingredient.of(Items.ENDER_EYE))
                .define('D', Ingredient.of(Items.ENDER_PEARL))
                .unlockedBy(getHasName(SBItems.SLASHBLADE_WOOD), has(SBItems.SLASHBLADE_WOOD)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.AGITO.location()).pattern(" S ").pattern("SBS")
                .pattern(" S ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.AGITO_RUST.location()).killCount(100).build()).toVanilla())
                .define('S', Ingredient.of(SBItems.PROUDSOUL))
                .unlockedBy(getHasName(SBItems.PROUDSOUL), has(SBItems.PROUDSOUL)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.OROTIAGITO_SEALED.location()).pattern(" S ")
                .pattern("SBS").pattern(" S ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.OROTIAGITO_RUST.location()).killCount(100).build()).toVanilla())
                .define('S', Ingredient.of(SBItems.PROUDSOUL))
                .unlockedBy(getHasName(SBItems.PROUDSOUL), has(SBItems.PROUDSOUL)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.OROTIAGITO.location()).pattern("PSP")
                .pattern("SBS").pattern("PSP")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.OROTIAGITO_SEALED.location()).killCount(1000)
                                .proudSoul(1000).refineCount(10).build()).toVanilla())
                .define('P', Ingredient.of(SBItems.PROUDSOUL)).define('S', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), has(SBItems.PROUDSOUL_SPHERE)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.DOUTANUKI.location()).pattern("  P")
                .pattern(" B ").pattern("P  ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.SABIGATANA.location()).killCount(100).proudSoul(1000)
                                .refineCount(10).build()).toVanilla())
                .define('P', Ingredient.of(SBItems.PROUDSOUL_SPHERE))
                .unlockedBy(getHasName(SBItems.PROUDSOUL_SPHERE), has(SBItems.PROUDSOUL_SPHERE)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.SABIGATANA.location()).pattern("  P")
                .pattern(" P ").pattern("B  ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(SlashBladeBuiltInRegistry.SABIGATANA.location()).addSwordType(SwordType.BROKEN)
                                .addSwordType(SwordType.SEALED).build()).toVanilla())
                .define('P', Ingredient.of(SBItems.PROUDSOUL_INGOT))
                .unlockedBy(getHasName(SBItems.PROUDSOUL_INGOT), has(SBItems.PROUDSOUL_INGOT)).save(consumer);

        SlashBladeShapedRecipeBuilder.shaped(SlashBladeBuiltInRegistry.TUKUMO.location()).pattern("ESD").pattern("RBL")
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
                .unlockedBy(getHasName(SBItems.SLASHBLADE), inventoryTrigger(ItemPredicate.Builder.item().withSubPredicate(
                        ItemPredicateRegistry.SLASHBLADE,
                        new SlashBladeItemPredicate(
                                RequestDefinition.Builder.newInstance().build()
                        )
                ))).save(consumer);

        rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_WOODEN.location(), Items.WOODEN_SWORD, consumer);
        rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_STONE.location(), Items.STONE_SWORD, consumer);
        rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_IRON.location(), Items.IRON_SWORD, consumer);
        rodaiRecipe(SlashBladeBuiltInRegistry.RODAI_GOLDEN.location(), Items.GOLDEN_SWORD, consumer);
        rodaiAdvRecipe(SlashBladeBuiltInRegistry.RODAI_DIAMOND.location(), Items.DIAMOND_SWORD, consumer);
        rodaiAdvRecipe(SlashBladeBuiltInRegistry.RODAI_NETHERITE.location(), Items.NETHERITE_SWORD, consumer);
    }

    private void rodaiRecipe(ResourceLocation rodai, ItemLike sword, RecipeOutput consumer) {
        SlashBladeShapedRecipeBuilder.shaped(rodai).pattern("  P").pattern(" B ").pattern("WS ").define('B',
                        SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                RequestDefinition.Builder.newInstance().killCount(100).addSwordType(SwordType.BROKEN).build()).toVanilla())
                .define('W', Ingredient.of(sword)).define('S', ConventionalItemTags.STRINGS)
                .define('P', Ingredient.of(SBItems.PROUDSOUL_CRYSTAL))
                .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                .save(consumer);
    }

    private void rodaiAdvRecipe(ResourceLocation rodai, ItemLike sword, RecipeOutput consumer) {
        SlashBladeShapedRecipeBuilder.shaped(rodai).pattern("  P").pattern(" B ").pattern("WS ").define('B',
                        SlashBladeIngredient.of(SBItems.SLASHBLADE_SILVERBAMBOO,
                                RequestDefinition.Builder.newInstance().killCount(100).addSwordType(SwordType.BROKEN).build()).toVanilla())
                .define('W', Ingredient.of(sword)).define('S', Ingredient.of(ConventionalItemTags.STRINGS))
                .define('P', Ingredient.of(SBItems.PROUDSOUL_TRAPEZOHEDRON))
                .unlockedBy(getHasName(SBItems.SLASHBLADE_SILVERBAMBOO), has(SBItems.SLASHBLADE_SILVERBAMBOO))
                .save(consumer);
    }

    private Holder<Enchantment> holder(ResourceKey<Enchantment> key) {
        if (this.provider == null) {
            provider = this.registries.join();
        }
        return this.provider.lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(key);
    }
}
