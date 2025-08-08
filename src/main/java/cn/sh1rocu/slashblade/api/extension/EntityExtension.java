package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface EntityExtension {
    default CompoundTag sb$getPersistentData() {
        throw new RuntimeException();
    }

    default void sb$deserializeNBT(CompoundTag nbt) {
        throw new RuntimeException();
    }

    default CompoundTag sb$serializeNBT() {
        throw new RuntimeException();
    }


    default Collection<ItemEntity> sb$captureDrops() {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }

    default Collection<ItemEntity> sb$captureDrops(Collection<ItemEntity> value) {
        throw new RuntimeException("this should be overridden via mixin. what?");
    }
}