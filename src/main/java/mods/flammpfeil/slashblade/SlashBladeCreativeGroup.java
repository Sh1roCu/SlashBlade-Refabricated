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
                ItemStack stack = new ItemStack(SBItems.slashblade);
                CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(s -> {
                    s.setModel(new ResourceLocation(SlashBlade.MODID, "model/named/yamato.obj"));
                    s.setTexture(new ResourceLocation(SlashBlade.MODID, "model/named/yamato.png"));
                });
                return stack;
            }).displayItems((features, output) -> {

                output.accept(SBItems.proudsoul);
                output.accept(SBItems.proudsoul_tiny);
                output.accept(SBItems.proudsoul_ingot);
                output.accept(SBItems.proudsoul_sphere);

                output.accept(SBItems.proudsoul_crystal);
                output.accept(SBItems.proudsoul_trapezohedron);
                fillEnchantmentsSouls(output);
                fillSASpheres(output);
                output.accept(SBItems.bladestand_1);
                output.accept(SBItems.bladestand_1w);
                output.accept(SBItems.bladestand_2);
                output.accept(SBItems.bladestand_2w);
                output.accept(SBItems.bladestand_s);
                output.accept(SBItems.bladestand_v);

                output.accept(SBItems.slashblade_wood);
                output.accept(SBItems.slashblade_bamboo);
                output.accept(SBItems.slashblade_silverbamboo);
                output.accept(SBItems.slashblade_white);
                output.accept(SBItems.slashblade);

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
            ItemStack blade = new ItemStack(SBItems.slashblade);
            //if (blade.canApplyAtEnchantingTable(enchantment)) {
            if (enchantment.canEnchant(blade)) {
                ItemStack soul = new ItemStack(SBItems.proudsoul_tiny);
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
            ItemStack sphere = new ItemStack(SBItems.proudsoul_sphere);
            CompoundTag tag = new CompoundTag();
            tag.putString("SpecialAttackType", key.toString());
            sphere.setTag(tag);
            output.accept(sphere);
        });
    }
}
