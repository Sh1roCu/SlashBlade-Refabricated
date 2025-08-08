package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.IEnchantment;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Unique
    private static Enchantment sb$currentEnchantment = null;

    @ModifyExpressionValue(
            method = "getAvailableEnchantmentResults",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
            )
    )
    private static Object sb$grabEnchantment(Object o) {
        if (o instanceof Enchantment e) {
            sb$currentEnchantment = e;
        }
        return o;
    }

    @WrapOperation(
            method = "getAvailableEnchantmentResults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean sb$canApplyAtEnchantingTable(
            EnchantmentCategory category, Item item, Operation<Boolean> original,
            int level, ItemStack stack, boolean allowTreasure
    ) {
        Enchantment enchantment = sb$currentEnchantment;
        if (enchantment != null && stack.getItem() instanceof IEnchantment ex) {
            return ex.canApplyAtEnchantingTable(stack, enchantment);
        }
        return original.call(category, item);
    }
}