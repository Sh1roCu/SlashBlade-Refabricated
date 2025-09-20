package cn.sh1rocu.slashblade.mixin.accessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {
    @Invoker("<init>")
    static ItemStack sb$create(ItemLike itemLike, int count, Optional<CompoundTag> optional) {
        throw new AssertionError();
    }
}
