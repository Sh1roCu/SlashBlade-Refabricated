package mods.flammpfeil.slashblade.event.drop;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDropsEvent;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class EntityDropEvent {
    public static void dropBlade(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        var bladeRegistry = SlashBlade.getSlashBladeDefinitionRegistry(entity.level());
        entity.level().registryAccess().registryOrThrow(EntityDropEntry.REGISTRY_KEY).forEach(entry -> {
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entry.getEntityType()))
                return;
            if (!bladeRegistry.containsKey(entry.getBladeName()))
                return;

            if (!(event.getSource().getEntity() instanceof LivingEntity))
                return;

            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

            if (SlashBladeConfig.FRIENDLY_ENABLE.get() || (entity instanceof Enemy)) {
                if (entry.isRequestSlashBladeKill()
                        && !(attacker.getMainHandItem().getItem() instanceof ItemSlashBlade))
                    return;
            }

            int lootingLevel = EnchantmentHelper.getEnchantmentLevel(attacker.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), attacker);
            float resultRate = Math.min(1F, entry.getDropRate() + lootingLevel * 0.1F);

            if (entry.isDropFixedPoint())
                dropBlade(entity, BuiltInRegistries.ENTITY_TYPE.get(entry.getEntityType()),
                        bladeRegistry.get(entry.getBladeName()).getBlade(), resultRate, entry.getDropPoint().x,
                        entry.getDropPoint().y, entry.getDropPoint().z);
            else
                dropBlade(entity, BuiltInRegistries.ENTITY_TYPE.get(entry.getEntityType()),
                        bladeRegistry.get(entry.getBladeName()).getBlade(), resultRate, entity.getX(), entity.getY(),
                        entity.getZ());
        });

    }

    public static void dropBlade(LivingEntity entity, EntityType<?> type, ItemStack blade, float percent, double x,
                                 double y, double z) {
        if (entity.getType().equals(type)) {
            var rand = entity.level().getRandom();

            if (rand.nextFloat() > percent)
                return;
            ItemEntity itementity = new ItemEntity(entity.level(), x, y, z, blade);
            BladeItemEntity e = new BladeItemEntity(SBEntityTypes.BladeItem, entity.level());

            e.restoreFrom(itementity);
            e.init();
            e.push(0, 0.4, 0);

            e.setPickUpDelay(20 * 2);
            e.setGlowingTag(true);

            e.setAirSupply(-1);

            e.setThrower(entity);

            entity.level().addFreshEntity(e);
        }
    }
}
