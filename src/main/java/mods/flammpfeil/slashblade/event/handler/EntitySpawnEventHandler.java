package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class EntitySpawnEventHandler {
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        LivingEntity entity = event.getEntity();
        boolean isZombie = isZombie(entity);
        if (!isZombie)
            return;
        if (!entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty())
            return;

        RandomSource random = event.getLevel().getRandom();
        float difficultyMultiplier = event.getDifficulty().getSpecialMultiplier();

        if (!RegistryHandler.DEFINITIONS.containsKey(SlashBladeBuiltInRegistry.SABIGATANA.identifier()))
            return;

        float rngResult = random.nextFloat();

        if (rngResult < SlashBladeConfig.BROKEN_SABIGATANA_SPAWN_CHANCE.get() * difficultyMultiplier) {
            if (rngResult < SlashBladeConfig.SABIGATANA_SPAWN_CHANCE.get() * difficultyMultiplier) {
                entity.setItemSlot(EquipmentSlot.MAINHAND,
                        RegistryHandler.DEFINITIONS.get(SlashBladeBuiltInRegistry.SABIGATANA.identifier()).getBlade());
            } else {
                entity.setItemSlot(EquipmentSlot.MAINHAND,
                        RegistryHandler.DEFINITIONS.get(SlashBladeBuiltInRegistry.SABIGATANA_BROKEN.identifier()).getBlade());
            }
        }
    }

    private static boolean isZombie(LivingEntity entity) {
        return entity instanceof Zombie && !(entity instanceof Drowned) && !(entity instanceof ZombifiedPiglin);
    }
}
