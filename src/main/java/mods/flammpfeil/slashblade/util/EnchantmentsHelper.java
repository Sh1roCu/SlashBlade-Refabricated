package mods.flammpfeil.slashblade.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentsHelper {
    //判断A是否含有B的附魔
    public static boolean hasEnchantmentsMatch(ItemStack stackA, ItemStack stackB) {
        ItemEnchantments enchantmentsB = EnchantmentHelper.getEnchantmentsForCrafting(stackB);

        // 如果B没有附魔要求，直接返回true
        if (enchantmentsB.isEmpty()) return true;

        ItemEnchantments enchantmentsA = EnchantmentHelper.getEnchantmentsForCrafting(stackA);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantmentsB.entrySet()) {
            Holder<Enchantment> ench = entry.getKey();
            int requiredLevel = entry.getIntValue();

            // 检查A是否包含该附魔且等级足够
            if (!enchantmentsA.keySet().contains(ench) || enchantmentsA.getLevel(ench) < requiredLevel) {
                return false;
            }
        }
        return true;
    }
}
