package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.LivingKnockBackEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class KnockBackHandler {

    static final String NBT_KEY = "knockback_factor";

    public static void setCancel(LivingEntity target) {
        setFactor(target, 0, 0, 0);
    }

    public static void setSmash(LivingEntity target, double horizontalFactor) {
        setFactor(target, horizontalFactor, 0, 0);
    }

    public static void setVertical(LivingEntity target, double verticalFactor) {
        setFactor(target, 0, verticalFactor, -verticalFactor);
    }

    public static void setFactor(LivingEntity target, double horizontalFactor, double verticalFactor,
                                 double addFallDistance) {
        NBTHelper.putVector3d(((EntityExtension) target).sb$getPersistentData(), NBT_KEY,
                new Vec3(horizontalFactor, verticalFactor, addFallDistance));
    }

    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        LivingEntity target = event.getEntity();

        CompoundTag nbt = ((EntityExtension) target).sb$getPersistentData();

        if (!nbt.contains(NBT_KEY))
            return;

        Vec3 factor = NBTHelper.getVector3d(nbt, NBT_KEY);
        nbt.remove(NBT_KEY);

        if (target.fallDistance < 0)
            target.fallDistance = 0;

        // z = falldistance factor
        target.fallDistance += factor.z;

        // movement factor is resistable
        if ((target.getRandom().nextDouble() < target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue()))
            return;

        target.hasImpulse = true;

        Vec3 motion = target.getDeltaMovement();

        // x = strength multiplier
        if (factor.x == 0) {
            event.setCanceled(true);

            motion = motion.multiply(0, 1, 0);
        } else
            event.setStrength((float) (event.getStrength() * factor.x));

        // y = vertical factor

        if (0 < factor.y) {
            target.setOnGround(false);
            event.getEntity().setDeltaMovement(motion.x, Math.max(motion.y, factor.y), motion.z);
        } else if (factor.y < 0) {
            event.getEntity().setDeltaMovement(motion.x, Math.min(motion.y, factor.y), motion.z);
        }
    }
}
