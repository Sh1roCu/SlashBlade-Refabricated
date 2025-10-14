package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.critereon.ItemAttributeModifiersPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemAttributeModifiersPredicate.class)
public class ItemAttributeModifiersPredicateMixin {
    @ModifyVariable(method = "matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/component/ItemAttributeModifiers;)Z", at = @At("HEAD"), argsOnly = true)
    private ItemAttributeModifiers sb$modifyItemAttributeModifier(ItemAttributeModifiers original, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof ItemSlashBladeExtension item) {
            return item.getDefaultAttributeModifiers(stack);
        }
        return original;
    }
}
