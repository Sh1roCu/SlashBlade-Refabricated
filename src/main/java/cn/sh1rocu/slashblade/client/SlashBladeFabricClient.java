package cn.sh1rocu.slashblade.client;

import cn.sh1rocu.slashblade.api.event.EntityAddedLayerCallback;
import mods.flammpfeil.slashblade.client.ClientHandler;
import mods.flammpfeil.slashblade.client.renderer.entity.*;
import mods.flammpfeil.slashblade.client.renderer.event.PreloadedModelEvent;
import mods.flammpfeil.slashblade.event.handler.MoveInputHandler;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SlashBladeFabricClient implements ClientModInitializer, ModelLoadingPlugin {

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this);
        PreloadedModelEvent.registerResourceLoaders();
        NetworkManager.registerS2CPackets();
        regisetEntityRenderers();
        ClientHandler.doClientStuff();
        EntityAddedLayerCallback.EVENT.register(ClientHandler::addLayers);
        ClientTickEvents.END_CLIENT_TICK.register(MoveInputHandler::onPlayerPostTick);
    }

    @Override
    public void onInitializeModelLoader(Context plugin) {
        plugin.modifyModelAfterBake().register(ClientHandler::Baked);
    }

    public static void regisetEntityRenderers() {
        EntityRendererRegistry.register(SBEntityTypes.SummonedSword, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.StormSwords, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.SpiralSwords, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BlisteringSwords, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.HeavyRainSwords, SummonedSwordRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.JudgementCut, JudgementCutRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BladeItem, BladeItemEntityRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.BladeStand, BladeStandEntityRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.SlashEffect, SlashEffectRenderer::new);
        EntityRendererRegistry.register(SBEntityTypes.Drive, DriveRenderer::new);
    }
}
