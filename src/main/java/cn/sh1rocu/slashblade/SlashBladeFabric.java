package cn.sh1rocu.slashblade;

import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDropsEvent;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import mods.flammpfeil.slashblade.client.ClientHandler;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import mods.flammpfeil.slashblade.event.bladestand.BlandStandEventHandler;
import mods.flammpfeil.slashblade.event.drop.EntityDropEvent;
import mods.flammpfeil.slashblade.event.handler.EntitySpawnEventHandler;
import mods.flammpfeil.slashblade.event.handler.RegistryHandler;
import mods.flammpfeil.slashblade.event.handler.SlashBladeEventHandler;
import mods.flammpfeil.slashblade.registry.specialeffects.WitherEdge;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class SlashBladeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SlashBlade.init();
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(SlashBladeCreativeGroup::onCreativeTagBuilding);
        BlandStandEventHandler.init();
        LivingDropsEvent.EVENT.register(EntityDropEvent::dropBlade);
        MobSpawnEvent.FINALIZE_SPAWN.register(EntitySpawnEventHandler::onMobSpawn);
        RegistryHandler.onDatapackRegister();
        RegistryHandler.registerSerializers();
        LivingAttackEvent.EVENT.register(SlashBladeEventHandler::onLivingOnFire);
        SlashBladeRegistryEvent.PRE.register(SlashBladeEventHandler::onLoadingBlade);
        SlashBladeEvent.UPDATE.register(WitherEdge::onSlashBladeUpdate);
        SlashBladeEvent.HIT.register(WitherEdge::onSlashBladeHit);
    }
}
