package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TargetSelector;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ArrowReflector {

    public static boolean isMatch(Entity arrow, Entity attacker) {
        if (arrow == null)
            return false;
        if (!(arrow instanceof Projectile))
            return false;

        return true;
    }

    public static void doReflect(Entity arrow, Entity attacker) {
        if (!isMatch(arrow, attacker))
            return;

        arrow.hurtMarked = true;
        if (attacker != null) {
            Vec3 dir = attacker.getLookAngle();

            do {
                if (attacker instanceof LivingEntity)
                    break;

                ItemStack stack = ((LivingEntity) attacker).getMainHandItem();

                if (stack.isEmpty())
                    break;
                if (!(stack.getItem() instanceof ItemSlashBlade))
                    break;

                Entity target = CapabilitySlashBlade.BLADESTATE.maybeGet(stack)
                        .map(s -> s.getTargetEntity(attacker.level())).orElse(null);
                if (target != null) {
                    dir = arrow.position().subtract(target.getEyePosition(1.0f)).normalize();
                } else {
                    dir = arrow.position()
                            .subtract(attacker.getLookAngle().scale(10).add(attacker.getEyePosition(1.0f))).normalize();
                }

            } while (false);

            ((Projectile) arrow).shoot(dir.x, dir.y, dir.z, 3.5f, 0.2f);

            if (arrow instanceof AbstractArrow)
                ((AbstractArrow) arrow).setCritArrow(true);

        }
    }

    public static void doTicks(LivingEntity attacker) {

        ItemStack stack = attacker.getMainHandItem();

        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(s -> {
            int ticks = attacker.getTicksUsingItem();

            if (ticks == 0)
                return;

            ResourceLocation old = s.getComboSeq();
            ResourceLocation current = s.resolvCurrentComboState(attacker);
            ComboState currentCS = ComboStateRegistry.COMBO_STATE.get(current) != null
                    ? ComboStateRegistry.COMBO_STATE.get(current)
                    : ComboStateRegistry.NONE;
            if (old != current) {
                ComboState oldCS = ComboStateRegistry.COMBO_STATE.get(current);
                ticks -= (int) TimeValueHelper.getTicksFromMSec(oldCS.getTimeoutMS());
            }

            double period = TimeValueHelper.getTicksFromFrames(currentCS.getEndFrame() - currentCS.getStartFrame())
                    * (1.0f / currentCS.getSpeed());

            if (ticks < period) {
                List<Entity> founds = TargetSelector.getReflectableEntitiesWithinAABB(attacker);

                founds.stream().filter(e -> (e instanceof Projectile) && ((Projectile) e).getOwner() != attacker)
                        .forEach(e -> doReflect(e, attacker));
            }
        });

    }

}
