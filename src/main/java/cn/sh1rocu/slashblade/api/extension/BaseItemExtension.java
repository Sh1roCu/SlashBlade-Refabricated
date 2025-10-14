package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface BaseItemExtension {
    default boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    int getEnchantmentValue(ItemStack stack);

}
