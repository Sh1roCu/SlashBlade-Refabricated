package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.nbt.CompoundTag;

public interface EntityExtension {
    default CompoundTag sb$getPersistentData() {
        throw new RuntimeException();
    }
}