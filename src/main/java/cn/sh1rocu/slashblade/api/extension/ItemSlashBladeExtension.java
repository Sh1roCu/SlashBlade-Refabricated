package cn.sh1rocu.slashblade.api.extension;

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
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);

    <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken);

    void setDamage(ItemStack stack, int damage);

    int getDamage(ItemStack stack);

    int getMaxDamage(ItemStack stack);

    boolean onEntitySwing(ItemStack stack, LivingEntity entity);

    boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);

    Rarity getRarity(ItemStack stack);

    @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack);
}
