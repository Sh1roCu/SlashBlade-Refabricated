package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TargetSelector;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ArrowReflector {

    public static boolean isMatch(Entity arrow, Entity attacker) {
        if (arrow == null)
            return false;
        return arrow instanceof Projectile;
    }

    public static void doReflect(Entity arrow, Entity attacker) {
        if (!isMatch(arrow, attacker))
            return;

        arrow.hurtMarked = true;
        if (attacker != null) {
            Vec3 dir = attacker.getLookAngle();

            do {
                if (!(attacker instanceof LivingEntity living)) {
                    break;
                }

                ItemStack stack = living.getMainHandItem();

                if (stack.isEmpty()) {
                    break;
                }
                if (!(stack.getItem() instanceof ItemSlashBlade)) {
                    break;
                }

                Entity target = CapabilitySlashBlade.getBladeState(stack)
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

        CapabilitySlashBlade.getBladeState(stack).ifPresent(s -> {
            int ticks = attacker.getTicksUsingItem();

            if (ticks == 0)
                return;

            Identifier old = s.getComboSeq();
            Identifier current = s.resolveCurrentComboState(attacker);
            ComboState currentCS = ComboStateRegistry.COMBO_STATE.get(current).isPresent()
                    ? ComboStateRegistry.COMBO_STATE.getValue(current)
                    : ComboStateRegistry.NONE;
            if (!old.equals(current)) {
                ComboState oldCS = ComboStateRegistry.COMBO_STATE.getValue(current);
                if (oldCS != null) {
                    ticks -= (int) TimeValueHelper.getTicksFromMSec(oldCS.getTimeoutMS());
                }
            }

            double period = 0;
            if (currentCS != null) {
                period = TimeValueHelper.getTicksFromFrames(currentCS.getEndFrame() - currentCS.getStartFrame())
                        * (1.0f / currentCS.getSpeed());
            }

            if (ticks < period) {
                List<Entity> founds = TargetSelector.getReflectableEntitiesWithinAABB(attacker);

                founds.stream().filter(e -> (e instanceof Projectile) && ((Projectile) e).getOwner() != attacker)
                        .forEach(e -> doReflect(e, attacker));
            }
        });

    }

}
