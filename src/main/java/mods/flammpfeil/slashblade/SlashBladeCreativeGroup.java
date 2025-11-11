package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Objects;

public class SlashBladeCreativeGroup {
    public static void init() {

    }

    private static final CreativeModeTab SLASHBLADE = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.slashblade")).icon(() -> {
                ItemStack stack = new ItemStack(SBItems.SLASHBLADE);
                CapabilitySlashBlade.getBladeState(stack).ifPresent(s -> {
                    s.setModel(ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/yamato.obj"));
                    s.setTexture(ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/named/yamato.png"));
                });
                return stack;
            }).displayItems((features, output) -> {

                output.accept(SBItems.PROUDSOUL);
                output.accept(SBItems.PROUDSOUL_TINY);
                output.accept(SBItems.PROUDSOUL_INGOT);
                output.accept(SBItems.PROUDSOUL_SPHERE);

                output.accept(SBItems.PROUDSOUL_CRYSTAL);
                output.accept(SBItems.PROUDSOUL_TRAPEZOHEDRON);
                fillEnchantmentsSouls(features, output);
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

    public static void onCreativeTagBuilding(CreativeModeTab group, FabricItemGroupEntries entries) {
        SlashBlade.getSlashBladeDefinitionRegistry(entries.getContext().holders())
                .listElements()
                .sorted(SlashBladeDefinition.COMPARATOR).forEach(entry -> {
                    if (!Objects.equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group), entry.value().getCreativeGroup()))
                        return;

                    var blade = entry.value().getBlade(entries.getContext().holders());
                    if (!blade.isEmpty())
                        entries.accept(blade);
                });
    }

    @Deprecated
    private static void fillBlades(CreativeModeTab.ItemDisplayParameters features, CreativeModeTab.Output output) {
        SlashBlade.getSlashBladeDefinitionRegistry(features.holders()).listElements()
                .sorted(SlashBladeDefinition.COMPARATOR).forEach(entry -> {
                    var blade = entry.value().getBlade(features.holders());
                    if (!blade.isEmpty())
                        output.accept(blade);
                });
    }

    private static void fillEnchantmentsSouls(CreativeModeTab.ItemDisplayParameters features, CreativeModeTab.Output output) {
        features.holders().lookupOrThrow(Registries.ENCHANTMENT).listElementIds().forEach(enchantment -> {
            ItemStack blade = new ItemStack(SBItems.SLASHBLADE);
            var holder = features.holders().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
            if (blade.canBeEnchantedWith(holder, EnchantingContext.ACCEPTABLE)) {
                ItemStack soul = new ItemStack(SBItems.PROUDSOUL_TINY);
                soul.enchant(holder, 1);
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
            CustomData.update(DataComponents.CUSTOM_DATA, sphere,
                    tag -> tag.putString("SpecialAttackType", key.toString()));
            output.accept(sphere);
        });
    }
}
