package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface BaseItemExtension {
    boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);

    int getEnchantmentValue(ItemStack stack);

}
