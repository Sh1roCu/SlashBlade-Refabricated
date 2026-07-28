package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.AttributeModifiersPredicate;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SingleComponentItemPredicate.class)
public interface SingleComponentItemPredicateMixin {
    @SuppressWarnings("ConstantConditions,rawtypes")
    @WrapMethod(method = "matches(Lnet/minecraft/core/component/DataComponentGetter;)Z")
    private boolean sb$matches(DataComponentGetter components, Operation<Boolean> original) {
        var self = (SingleComponentItemPredicate) this;
        if (!(self instanceof AttributeModifiersPredicate predicate)) {
            return original.call(components);
        }

        if (components instanceof ItemStack stack && stack.getItem() instanceof ItemSlashBladeExtension extension) {
            return predicate.matches(extension.getDefaultAttributeModifiers(stack));
        }

        return original.call(components);
    }
}