package cn.sh1rocu.slashblade.util;

import cn.sh1rocu.slashblade.api.event.*;
import cn.sh1rocu.slashblade.mixin.accessor.AnvilMenuAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class CommonHooks {
    public static boolean onAnvilChange(AnvilMenu container, ItemStack left, ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        var e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        AnvilUpdateEvent.EVENT.invoker().onAnvilUpdate(e);
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
        var e = new AnvilRepairEvent(player, left, right, output);
        AnvilRepairEvent.EVENT.invoker().onAnvilRepair(e);
        return e.getBreakChance();
    }

    public static void onLivingJump(LivingEntity entity) {
        LivingJumpEvent.EVENT.invoker().post(new LivingJumpEvent(entity));
    }

    public static int getExperienceDrop(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
        var event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        LivingExperienceDropEvent.EVENT.invoker().post(event);
        if (event.isCanceled()) {
            return 0;
        }
        return event.getDroppedExperience();
    }

    public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ) {
        var event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
        LivingKnockBackEvent.EVENT.invoker().post(event);
        return event;
    }
}
