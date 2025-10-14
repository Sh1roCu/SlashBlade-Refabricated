package cn.sh1rocu.slashblade.util;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionUtils {
    public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag compoundTag) {
        List<MobEffectInstance> list = Lists.newArrayList();
        var potion = getPotion(compoundTag);
        if (potion != null) {
            list.addAll(potion.getEffects());
        }
        getCustomEffects(compoundTag, list);
        return list;
    }

    public static void getCustomEffects(@Nullable CompoundTag compoundTag, List<MobEffectInstance> list) {
        if (compoundTag != null && compoundTag.contains("CustomPotionEffects", 9)) {
            ListTag listTag = compoundTag.getList("CustomPotionEffects", 10);

            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                MobEffectInstance mobEffectInstance = MobEffectInstance.load(compoundTag2);
                if (mobEffectInstance != null) {
                    list.add(mobEffectInstance);
                }
            }
        }

    }

    public static @Nullable Potion getPotion(@Nullable CompoundTag compoundTag) {
        return compoundTag == null ? null : BuiltInRegistries.POTION.get(ResourceLocation.parse(compoundTag.getString("Potion")));
    }
}
