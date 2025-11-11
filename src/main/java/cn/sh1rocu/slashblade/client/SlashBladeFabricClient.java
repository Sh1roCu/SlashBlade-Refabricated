package cn.sh1rocu.slashblade.client;

import cn.sh1rocu.slashblade.api.event.EntityAddedLayerCallback;
import cn.sh1rocu.slashblade.api.event.RenderTickEvent;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import mods.flammpfeil.slashblade.ability.LockOnManager;
import mods.flammpfeil.slashblade.client.ClientHandler;
import mods.flammpfeil.slashblade.client.renderer.entity.*;
import mods.flammpfeil.slashblade.client.renderer.event.PreloadedModelEvent;
import mods.flammpfeil.slashblade.event.handler.BlockPickCanceller;
import mods.flammpfeil.slashblade.event.handler.MoveInputHandler;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;

public class SlashBladeFabricClient implements ClientModInitializer, ModelLoadingPlugin {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this);
        PreloadedModelEvent.registerResourceLoaders();
        NetworkManager.registerS2CPackets();
        regisetEntityRenderers();
        ClientHandler.doClientStuff();

        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof ItemSlashBladeExtension).forEach(clientEx ->
                BuiltinItemRendererRegistry.INSTANCE.register(clientEx,
                        (stack, mode, matrices, vertexConsumers, light, overlay) ->
                                ((ItemSlashBladeExtension) clientEx).getCustomRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay)));

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(ClientHandler::onCreativeTagBuilding);
        EntityAddedLayerCallback.EVENT.register(ClientHandler::addLayers);
        ClientTickEvents.END_CLIENT_TICK.register(MoveInputHandler::onPlayerPostTick);
        RenderTickEvent.START.register(LockOnManager.Client::onEntityUpdate);
        BlockPickCanceller.getInstance().register();
    }

    @Override
    public void onInitializeModelLoader(Context plugin) {
        plugin.modifyModelAfterBake().register(ClientHandler::Baked);
    }

    public static void regisetEntityRenderers() {
        EntityRendererRegistry.register(SBEntityTypes.SUMMONED_SWORD, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.STORM_SWORDS, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.SPIRAL_SWORDS, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BLISTERING_SWORDS, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.HEAVY_RAIN_SWORDS, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.JUDGEMENT_CUT, JudgementCutRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BLADE_ITEM, BladeItemEntityRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BLADE_STAND, BladeStandEntityRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.SLASH_EFFECT, SlashEffectRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.DRIVE, DriveRenderer::new);
    }
}
