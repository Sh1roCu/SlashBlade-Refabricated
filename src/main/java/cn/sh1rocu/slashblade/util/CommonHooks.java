package cn.sh1rocu.slashblade.util;

import cn.sh1rocu.slashblade.api.event.AnvilRepairEvent;
import cn.sh1rocu.slashblade.api.event.AnvilUpdateEvent;
import cn.sh1rocu.slashblade.mixin.accessor.AnvilMenuAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class CommonHooks {
    public static boolean onAnvilChange(AnvilMenu container, ItemStack left, ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        AnvilUpdateEvent.CALLBACK.invoker().onAnvilUpdate(e);
        if (e.isCanceled()) {
            outputSlot.setItem(0, ItemStack.EMPTY);
            ((AnvilMenuAccessor) container).sb$getCost().set((int) Mth.clamp(0, 0L, Integer.MAX_VALUE));
            ((AnvilMenuAccessor) container).sb$setRepairItemCountCost(0);
            return false;
        }
        if (e.getOutput().isEmpty())
            return true;

        outputSlot.setItem(0, e.getOutput());
        ((AnvilMenuAccessor) container).sb$getCost().set((int) Mth.clamp(e.getCost(), 0L, Integer.MAX_VALUE));
        ((AnvilMenuAccessor) container).sb$setRepairItemCountCost(e.getMaterialCost());
        return false;
    }

    public static float onAnvilRepair(Player player, ItemStack output, ItemStack left, ItemStack right) {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        AnvilRepairEvent.CALLBACK.invoker().onAnvilRepair(e);
        return e.getBreakChance();
    }
}
