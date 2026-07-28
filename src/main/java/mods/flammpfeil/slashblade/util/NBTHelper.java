package mods.flammpfeil.slashblade.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;

public class NBTHelper {

    public static Vec3 getVector3d(CompoundTag tag, String key) {
        ListTag listnbt = tag.getListOrEmpty(key);
        return new Vec3(
                listnbt.getDoubleOr(0, 0),
                listnbt.getDoubleOr(1, 0),
                listnbt.getDoubleOr(2, 0));
    }

    public static void putVector3d(CompoundTag tag, String key, Vec3 value) {
        tag.put(key, newDoubleNBTList(value.x, value.y, value.z));
    }

    public static ListTag newDoubleNBTList(Vec3 value) {
        return newDoubleNBTList(value.x, value.y, value.z);
    }

    public static ListTag newDoubleNBTList(double... numbers) {
        ListTag listnbt = new ListTag();

        for (double dValue : numbers) {
            listnbt.add(DoubleTag.valueOf(dValue));
        }

        return listnbt;
    }
}
