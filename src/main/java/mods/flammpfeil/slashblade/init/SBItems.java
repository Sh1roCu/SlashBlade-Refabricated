package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.*;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static mods.flammpfeil.slashblade.SlashBladeConfig.TRAPEZOHEDRON_MAX_REFINE;

public class SBItems {
    public static void init() {

    }

    public static final Item PROUDSOUL = register("proudsoul", id ->
            new ItemProudSoul(new Item.Properties().enchantable(50).setId(key(id))));
    public static final Item PROUDSOUL_INGOT = register("proudsoul_ingot", id ->
            new ItemProudSoul(new Item.Properties().enchantable(100).setId(key(id))));
    public static final Item PROUDSOUL_TINY = register("proudsoul_tiny", id ->
            new ItemProudSoul(new Item.Properties().enchantable(10).setId(key(id))));
    public static final Item PROUDSOUL_SPHERE = register("proudsoul_sphere", id ->
            new ItemProudSoul(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .enchantable(150)
                    .setId(key(id))) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                    if (data != null) {
                        CompoundTag tag = data.copyTag();
                        if (tag.contains("SpecialAttackType")) {
                            Identifier SA = Identifier.parse(tag.getStringOr("SpecialAttackType", ""));
                            if (SlashArtsRegistry.SLASH_ARTS.containsKey(SA) && !Objects.equals(SlashArtsRegistry.SLASH_ARTS.getValue(SA), SlashArtsRegistry.NONE)) {
                                builder.accept(Component.translatable("slashblade.tooltip.slash_art", SlashArtsRegistry.SLASH_ARTS.getValue(SA).getDescription()).withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                    super.appendHoverText(stack, context, display, builder, tooltipFlag);
                }
            });
    public static final Item PROUDSOUL_CRYSTAL = register("proudsoul_crystal", id ->
            new ItemProudSoul(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .enchantable(200)
                    .setId(key(id))) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                    if (data != null) {
                        CompoundTag tag = data.copyTag();
                        if (tag.contains("SpecialEffectType")) {
                            Minecraft mcinstance = Minecraft.getInstance();
                            Player player = mcinstance.player;
                            Identifier se = Identifier.parse(tag.getStringOr("SpecialEffectType", ""));
                            if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
                                builder.accept(Component.translatable("slashblade.tooltip.special_effect", SpecialEffect.getDescription(se),
                                                Component.literal(String.valueOf(SpecialEffect.getRequestLevel(se)))
                                                        .withStyle(SpecialEffect.isEffective(se, player.experienceLevel) ? ChatFormatting.RED
                                                                : ChatFormatting.DARK_GRAY))
                                        .withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                    super.appendHoverText(stack, context, display, builder, tooltipFlag);
                }
            });
    public static final Item PROUDSOUL_TRAPEZOHEDRON = register("proudsoul_trapezohedron", id ->
            new ItemProudSoul(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .enchantable(TRAPEZOHEDRON_MAX_REFINE.get())
                    .setId(key(id))));

    public static final Item SLASHBLADE_WOOD = register("slashblade_wood", id ->
            new ItemSlashBladeDetune(new ItemTierSlashBlade(60, 2F).toToolMaterial(), 2, 0.0F,
                    new Item.Properties().setId(key(id))).setDestructable()
                    .setTexture(SlashBlade.prefix("model/wood.png")));
    public static final Item SLASHBLADE_BAMBOO = register("slashblade_bamboo", id ->
            new ItemSlashBladeDetune(new ItemTierSlashBlade(70, 3F).toToolMaterial(), 3, 0.0F,
                    new Item.Properties().setId(key(id))).setDestructable()
                    .setTexture(SlashBlade.prefix("model/bamboo.png")));
    public static final Item SLASHBLADE_SILVERBAMBOO = register("slashblade_silverbamboo", id ->
            new ItemSlashBladeDetune(new ItemTierSlashBlade(40, 3F).toToolMaterial(), 3, 0.0F,
                    new Item.Properties().setId(key(id))).setTexture(SlashBlade.prefix("model/silverbamboo.png")));
    public static final Item SLASHBLADE_WHITE = register("slashblade_white", id ->
            new ItemSlashBladeDetune(new ItemTierSlashBlade(70, 4F).toToolMaterial(), 4, 0.0F,
                    new Item.Properties().setId(key(id))).setTexture(SlashBlade.prefix("model/white.png")));

    public static final Item SLASHBLADE = register("slashblade", id ->
            new ItemSlashBlade(new ItemTierSlashBlade(40, 4F).toToolMaterial(), 4, 0.0F,
                    new Item.Properties().setId(key(id))));

    public static final Item BLADESTAND_1 = register("bladestand_1", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id))));
    public static final Item BLADESTAND_2 = register("bladestand_2", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id))));
    public static final Item BLADESTAND_V = register("bladestand_v", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id))));
    public static final Item BLADESTAND_S = register("bladestand_s", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id))));
    public static final Item BLADESTAND_1_W = register("bladestand_1w", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id)), true));
    public static final Item BLADESTAND_2_W = register("bladestand_2w", id ->
            new BladeStandItem((new Item.Properties()).rarity(Rarity.COMMON).setId(key(id)), true));

    private static Item register(String name, Function<Identifier, Item> func) {
        Identifier id = SlashBlade.prefix(name);
        return Registry.register(BuiltInRegistries.ITEM, id, func.apply(id));
    }

    private static ResourceKey<Item> key(Identifier id) {
        return ResourceKey.create(Registries.ITEM, id);
    }
}
