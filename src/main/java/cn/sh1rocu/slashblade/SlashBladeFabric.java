package cn.sh1rocu.slashblade;

import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import mods.flammpfeil.slashblade.event.bladestand.BlandStandEventHandler;
import mods.flammpfeil.slashblade.event.handler.EntitySpawnEventHandler;
import mods.flammpfeil.slashblade.event.handler.RegistryHandler;
import mods.flammpfeil.slashblade.event.handler.SlashBladeEventHandler;
import mods.flammpfeil.slashblade.recipe.RecipeSerializerRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.WitherEdge;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.ItemStack;

public class SlashBladeFabric implements ModInitializer {

    public static HolderLookup.Provider SERVER_ACCESS = VanillaRegistries.createLookup();

    @Override
    public void onInitialize() {
        SlashBlade.init();
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> SERVER_ACCESS = minecraftServer.registryAccess());
        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> item instanceof ItemSlashBladeExtension, (builder, item) -> {
                ItemStack stack = new ItemStack(item);
                builder.set(DataComponents.ATTRIBUTE_MODIFIERS, ((ItemSlashBladeExtension) item).getDefaultAttributeModifiers(stack));
            });
        });
        RecipeSynchronization.synchronizeRecipeSerializer(RecipeSerializerRegistry.SLASHBLADE_SHAPED);
        RecipeSynchronization.synchronizeRecipeSerializer(RecipeSerializerRegistry.SLASHBLADE_SMITHING);
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register(SlashBladeCreativeGroup::onCreativeTagBuilding);
        BlandStandEventHandler.init();
        MobSpawnEvent.FINALIZE_SPAWN.register(EntitySpawnEventHandler::onMobSpawn);
        RegistryHandler.onDatapackRegister();
        RegistryHandler.registerSerializers();
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(SlashBladeEventHandler::onLivingOnFire);
        SlashBladeRegistryEvent.PRE.register(SlashBladeEventHandler::onLoadingBlade);
        SlashBladeEvent.CHARGE_ACTION.register(SlashBladeEventHandler::onChargeBlade);
        SlashBladeEvent.UPDATE.register(WitherEdge::onSlashBladeUpdate);
        SlashBladeEvent.HIT.register(WitherEdge::onSlashBladeHit);
    }
}
