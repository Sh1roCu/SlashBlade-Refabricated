package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IEnchantment {
    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.canEnchant(stack);
    }
}