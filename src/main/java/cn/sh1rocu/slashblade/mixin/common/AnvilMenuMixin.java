package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.util.CommonHooks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    @Nullable
    private String itemName;
    @Unique
    private static final ThreadLocal<Float> sb$breakChance = new ThreadLocal<>();

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0), cancellable = true)
    private void sb$onAnvilUpdate(CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack, @Local(ordinal = 1) int j) {
        if (!itemStack.isEmpty()) {
            if (!CommonHooks.onAnvilChange((AnvilMenu) (Object) this, itemStack, this.inputSlots.getItem(1), resultSlots, this.itemName, j, this.player)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private void sb$getForgeBreakChance(Player player, ItemStack stack, CallbackInfo ci) {
        sb$breakChance.set(CommonHooks.onAnvilRepair(player, stack, this.inputSlots.getItem(0), this.inputSlots.getItem(1)));
    }

    @ModifyExpressionValue(method = "method_24922", at = @At(value = "CONSTANT", args = "floatValue=0.12"))
    private static float sb$useForgeBreakChanceIfPossible(float original) {
        if (original == 0.12f) {
            var value = sb$breakChance.get();
            sb$breakChance.remove();
            return value;
        }
        return original;
    }
}
