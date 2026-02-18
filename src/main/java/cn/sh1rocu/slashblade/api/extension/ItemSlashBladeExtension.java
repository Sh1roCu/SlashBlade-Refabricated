package cn.sh1rocu.slashblade.api.extension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ItemSlashBladeExtension {
    default boolean sb$onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }

    default void sb$setDamage(ItemStack stack, int damage) {
        stack.set(DataComponents.DAMAGE, Mth.clamp(damage, 0, stack.getMaxDamage()));
    }

    default <T extends LivingEntity> int sb$damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return amount;
    }

    default int sb$getDamage(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(DataComponents.DAMAGE, 0), 0, stack.getMaxDamage());
    }

    default int sb$getMaxDamage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
    }

    default boolean sb$onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    default boolean sb$onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    BlockEntityWithoutLevelRenderer getCustomRenderer();

    Rarity getRarity(ItemStack stack);

    default @NotNull ItemAttributeModifiers sb$getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return ((Item) this).getDefaultAttributeModifiers();
    }
}
