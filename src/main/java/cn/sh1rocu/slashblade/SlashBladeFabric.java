package cn.sh1rocu.slashblade;

import cn.sh1rocu.slashblade.api.event.LivingAttackEvent;
import cn.sh1rocu.slashblade.api.event.LivingDropsEvent;
import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import mods.flammpfeil.slashblade.event.bladestand.BlandStandEventHandler;
import mods.flammpfeil.slashblade.event.drop.EntityDropEvent;
import mods.flammpfeil.slashblade.event.handler.EntitySpawnEventHandler;
import mods.flammpfeil.slashblade.event.handler.RegistryHandler;
import mods.flammpfeil.slashblade.event.handler.SlashBladeEventHandler;
import mods.flammpfeil.slashblade.registry.specialeffects.WitherEdge;
import net.fabricmc.api.ModInitializer;

public class SlashBladeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SlashBlade.init();
        BlandStandEventHandler.init();
        LivingDropsEvent.CALLBACK.register(EntityDropEvent::dropBlade);
        MobSpawnEvent.FINALIZE_SPAWN.register(EntitySpawnEventHandler::onMobSpawn);
        RegistryHandler.onDatapackRegister();
        RegistryHandler.registerSerializers();
        LivingAttackEvent.CALLBACK.register(SlashBladeEventHandler::onLivingOnFire);
        SlashBladeRegistryEvent.PRE.register(SlashBladeEventHandler::onLoadingBlade);
        SlashBladeBaseEvent.UPDATE.register(WitherEdge::onSlashBladeUpdate);
        SlashBladeBaseEvent.HIT.register(WitherEdge::onSlashBladeHit);
    }
}
