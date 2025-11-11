package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SlashBladeCreativeGroup {
    public static void init() {

    }

    private static final CreativeModeTab SLASHBLADE = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.slashblade")).icon(() -> {
                ItemStack stack = new ItemStack(SBItems.SLASHBLADE);
                CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(s -> {
                    s.setModel(new ResourceLocation(SlashBlade.MODID, "model/named/yamato.obj"));
                    s.setTexture(new ResourceLocation(SlashBlade.MODID, "model/named/yamato.png"));
                });
                return stack;
            }).displayItems((features, output) -> {

                output.accept(SBItems.PROUDSOUL);
                output.accept(SBItems.PROUDSOUL_TINY);
                output.accept(SBItems.PROUDSOUL_INGOT);
                output.accept(SBItems.PROUDSOUL_SPHERE);

                output.accept(SBItems.PROUDSOUL_CRYSTAL);
                output.accept(SBItems.PROUDSOUL_TRAPEZOHEDRON);
                fillEnchantmentsSouls(output);
                fillSASpheres(output);
                output.accept(SBItems.BLADESTAND_1);
                output.accept(SBItems.BLADESTAND_1_W);
                output.accept(SBItems.BLADESTAND_2);
                output.accept(SBItems.BLADESTAND_2_W);
                output.accept(SBItems.BLADESTAND_S);
                output.accept(SBItems.BLADESTAND_V);

                output.accept(SBItems.SLASHBLADE_WOOD);
                output.accept(SBItems.SLASHBLADE_BAMBOO);
                output.accept(SBItems.SLASHBLADE_SILVERBAMBOO);
                output.accept(SBItems.SLASHBLADE_WHITE);
                output.accept(SBItems.SLASHBLADE);

                // fillBlades(features, output);
            }).build();

    public static final CreativeModeTab SLASHBLADE_GROUP = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            SlashBlade.prefix("slashblade"),
            SLASHBLADE
    );

    @SuppressWarnings("unused")
    @Deprecated
    private static void fillBlades(CreativeModeTab.ItemDisplayParameters features, CreativeModeTab.Output output) {
        SlashBlade.getSlashBladeDefinitionRegistry(features.holders()).listElements()
                .sorted(SlashBladeDefinition.COMPARATOR).forEach(entry -> {
                    if (!entry.value().getBlade().isEmpty())
                        output.accept(entry.value().getBlade());
                });
    }

    private static void fillEnchantmentsSouls(CreativeModeTab.Output output) {
        BuiltInRegistries.ENCHANTMENT.forEach(enchantment -> {
            ItemStack blade = new ItemStack(SBItems.SLASHBLADE);
            //if (blade.canApplyAtEnchantingTable(enchantment)) {
            if (enchantment.canEnchant(blade)) {
                ItemStack soul = new ItemStack(SBItems.PROUDSOUL_TINY);
                soul.enchant(enchantment, 1);
                output.accept(soul);
            }

        });
    }

    private static void fillSASpheres(CreativeModeTab.Output output) {
        SlashArtsRegistry.SLASH_ARTS.forEach(slashArts -> {
            ResourceLocation key = SlashArtsRegistry.SLASH_ARTS.getKey(slashArts);
            if (slashArts.equals(SlashArtsRegistry.NONE) || key == null)
                return;
            ItemStack sphere = new ItemStack(SBItems.PROUDSOUL_SPHERE);
            CompoundTag tag = new CompoundTag();
            tag.putString("SpecialAttackType", key.toString());
            sphere.setTag(tag);
            output.accept(sphere);
        });
    }
}
