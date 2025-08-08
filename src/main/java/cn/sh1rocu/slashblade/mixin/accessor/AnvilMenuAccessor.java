package cn.sh1rocu.slashblade.mixin.accessor;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilMenu.class)
public interface AnvilMenuAccessor {
    @Accessor("cost")
    DataSlot sb$getCost();

    @Accessor("repairItemCountCost")
    void sb$setRepairItemCountCost(int repairItemCountCost);
}
