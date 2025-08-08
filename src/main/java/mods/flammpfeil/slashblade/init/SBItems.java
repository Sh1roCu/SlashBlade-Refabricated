package mods.flammpfeil.slashblade.init;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.item.SlashBladeBaseItem;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.item.BladeStandItem;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBladeDetune;
import mods.flammpfeil.slashblade.item.ItemTierSlashBlade;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static mods.flammpfeil.slashblade.SlashBladeConfig.TRAPEZOHEDRON_MAX_REFINE;

public class SBItems {
    public static void init() {

    }

    public static final Item proudsoul = register("proudsoul", new SlashBladeBaseItem(new FabricItemSettings()) {
        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            if (entity instanceof BladeItemEntity)
                return false;

            CompoundTag tag = ((EntityExtension) entity).sb$serializeNBT();
            tag.putInt("Health", 50);
            ((EntityExtension) entity).sb$deserializeNBT(tag);

            if (entity.isCurrentlyGlowing()) {
                entity.setDeltaMovement(
                        entity.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(0.0D, +0.04D, 0.0D));
            } else if (entity.isOnFire()) {
                entity.setDeltaMovement(
                        entity.getDeltaMovement().multiply(0.8, 0.5, 0.8).add(0.0D, +0.04D, 0.0D));
            }

            return false;
        }


        @Override
        public boolean isFoil(@NotNull ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return 50;
        }
    });
    public static final Item proudsoul_ingot = register("proudsoul_ingot", new SlashBladeBaseItem(new FabricItemSettings()) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return 100;
        }
    });
    public static final Item proudsoul_tiny = register("proudsoul_tiny", new SlashBladeBaseItem(new FabricItemSettings()) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return 10;
        }
    });
    public static final Item proudsoul_sphere = register("proudsoul_sphere", new SlashBladeBaseItem(new FabricItemSettings().rarity(Rarity.UNCOMMON)) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return 150;
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
            if (stack.getTag() != null) {
                CompoundTag tag = stack.getTag();
                if (tag.contains("SpecialAttackType")) {
                    ResourceLocation SA = new ResourceLocation(tag.getString("SpecialAttackType"));
                    if (SlashArtsRegistry.SLASH_ARTS.containsKey(SA) && !Objects.equals(SlashArtsRegistry.SLASH_ARTS.get(SA), SlashArtsRegistry.NONE)) {
                        components.add(Component.translatable("slashblade.tooltip.slash_art", SlashArtsRegistry.SLASH_ARTS.get(SA).getDescription()).withStyle(ChatFormatting.GRAY));
                    }
                }
            }
            super.appendHoverText(stack, level, components, flag);
        }
    });
    public static final Item proudsoul_crystal = register("proudsoul_crystal", new SlashBladeBaseItem(new FabricItemSettings().rarity(Rarity.RARE)) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return 200;
        }

        @Override
        @Environment(EnvType.CLIENT)
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if (stack.getTag() != null) {
                CompoundTag tag = stack.getTag();
                if (tag.contains("SpecialEffectType")) {
                    Minecraft mcinstance = Minecraft.getInstance();
                    Player player = mcinstance.player;
                    ResourceLocation se = new ResourceLocation(tag.getString("SpecialEffectType"));
                    if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
                        components.add(Component.translatable("slashblade.tooltip.special_effect", SpecialEffect.getDescription(se),
                                        Component.literal(String.valueOf(SpecialEffect.getRequestLevel(se)))
                                                .withStyle(SpecialEffect.isEffective(se, player.experienceLevel) ? ChatFormatting.RED
                                                        : ChatFormatting.DARK_GRAY))
                                .withStyle(ChatFormatting.GRAY));
                    }
                }
            }
            super.appendHoverText(stack, level, components, flag);
        }
    });
    public static final Item proudsoul_trapezohedron = register("proudsoul_trapezohedron", new SlashBladeBaseItem(new FabricItemSettings().rarity(Rarity.EPIC)) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;// super.hasEffect(stack);
        }

        @Override
        public int getEnchantmentValue(ItemStack stack) {
            return TRAPEZOHEDRON_MAX_REFINE.get();
        }
    });

    public static final Item slashblade_wood = register("slashblade_wood", new ItemSlashBladeDetune(new ItemTierSlashBlade(60, 2F), 2, 0.0F,
            new FabricItemSettings()).setDestructable()
            .setTexture(SlashBlade.prefix("model/wood.png")));
    public static final Item slashblade_bamboo = register("slashblade_bamboo", new ItemSlashBladeDetune(new ItemTierSlashBlade(70, 3F), 3, 0.0F,
            new FabricItemSettings()).setDestructable()
            .setTexture(SlashBlade.prefix("model/bamboo.png")));
    public static final Item slashblade_silverbamboo = register("slashblade_silverbamboo", new ItemSlashBladeDetune(new ItemTierSlashBlade(40, 3F), 3, 0.0F,
            new FabricItemSettings()).setTexture(SlashBlade.prefix("model/silverbamboo.png")));
    public static final Item slashblade_white = register("slashblade_white", new ItemSlashBladeDetune(new ItemTierSlashBlade(70, 4F), 4, 0.0F,
            new FabricItemSettings()).setTexture(SlashBlade.prefix("model/white.png")));

    public static final Item slashblade = register("slashblade", new ItemSlashBlade(new ItemTierSlashBlade(40, 4F), 4, 0.0F, new FabricItemSettings()));

    public static final Item bladestand_1 = register("bladestand_1", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON)));
    public static final Item bladestand_2 = register("bladestand_2", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON)));
    public static final Item bladestand_v = register("bladestand_v", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON)));
    public static final Item bladestand_s = register("bladestand_s", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON)));
    public static final Item bladestand_1w = register("bladestand_1w", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON), true));
    public static final Item bladestand_2w = register("bladestand_2w", new BladeStandItem((new FabricItemSettings()).rarity(Rarity.COMMON), true));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, SlashBlade.prefix(name), item);
    }
}
