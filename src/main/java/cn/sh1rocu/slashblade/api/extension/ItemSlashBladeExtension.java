package cn.sh1rocu.slashblade.api.extension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemSlashBladeExtension {
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);

    void setDamage(ItemStack stack, int damage);

    int getDamage(ItemStack stack);

    int getMaxDamage(ItemStack stack);

    boolean onEntitySwing(ItemStack stack, LivingEntity entity);

    boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);

    @Environment(EnvType.CLIENT)
    BlockEntityWithoutLevelRenderer getCustomRenderer();
}
