package cn.sh1rocu.slashblade.api.extension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ItemSlashBladeExtension {
    default boolean sb$onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }

    default void sb$setDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }

    default <T extends LivingEntity> int sb$damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    default int sb$getDamage(ItemStack stack) {
        return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
    }

    default int sb$getMaxDamage(ItemStack stack) {
        return ((Item) this).getMaxDamage();
    }

    default boolean sb$onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    default boolean sb$onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    BlockEntityWithoutLevelRenderer getCustomRenderer();
}
