package cn.sh1rocu.slashblade.client;

import cn.sh1rocu.slashblade.api.event.RenderFrameEvent;
import cn.sh1rocu.slashblade.util.neoforge.objloader.ObjLoader;
import mods.flammpfeil.slashblade.ability.LockOnManager;
import mods.flammpfeil.slashblade.client.ClientHandler;
import mods.flammpfeil.slashblade.client.renderer.entity.*;
import mods.flammpfeil.slashblade.client.renderer.event.PreloadedModelEvent;
import mods.flammpfeil.slashblade.client.renderer.special.*;
import mods.flammpfeil.slashblade.compat.iris.IrisCompat;
import mods.flammpfeil.slashblade.event.handler.BlockPickCanceller;
import mods.flammpfeil.slashblade.event.handler.MoveInputHandler;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.server.packs.PackType;

public class SlashBladeFabricClient implements ClientModInitializer, ModelLoadingPlugin {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            UnbakedModelDeserializer.register(ObjLoader.ID, ObjLoader.INSTANCE);
            ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ObjLoader.ID, ObjLoader.INSTANCE);
        });
        ClientRecipeSynchronizedEvent.EVENT.register(ClientRecipeEvent::onRecipeReceived);
        ModelLoadingPlugin.register(this);
        PreloadedModelEvent.registerResourceLoaders();
        NetworkManager.registerClientReceivers();
        regisetEntityRenderers();
        registerSpecialModelRenderers();
        ClientHandler.doClientStuff();

        IrisCompat.init();

        LivingEntityRenderLayerRegistrationCallback.EVENT.register(ClientHandler::addLayers);
        ClientTickEvents.END_CLIENT_TICK.register(MoveInputHandler::onPlayerPostTick);
        RenderFrameEvent.START.register(LockOnManager.Client::onEntityUpdate);
        BlockPickCanceller.getInstance().register();
    }

    @Override
    public void initialize(Context plugin) {
        plugin.modifyItemModelAfterBake().register(ClientHandler::baked);
    }

    private static void regisetEntityRenderers() {
        EntityRenderers.register(SBEntityTypes.SUMMONED_SWORD, SummonedSwordRenderer::new);
        EntityRenderers.register(SBEntityTypes.STORM_SWORDS, SummonedSwordRenderer::new);
        EntityRenderers.register(SBEntityTypes.SPIRAL_SWORDS, SummonedSwordRenderer::new);
        EntityRenderers.register(SBEntityTypes.BLISTERING_SWORDS, SummonedSwordRenderer::new);
        EntityRenderers.register(SBEntityTypes.HEAVY_RAIN_SWORDS, SummonedSwordRenderer::new);
        EntityRenderers.register(SBEntityTypes.JUDGEMENT_CUT, JudgementCutRenderer::new);
        EntityRenderers.register(SBEntityTypes.BLADE_ITEM, BladeItemEntityRenderer::new);
        EntityRenderers.register(SBEntityTypes.BLADE_STAND, BladeStandEntityRenderer::new);
        EntityRenderers.register(SBEntityTypes.SLASH_EFFECT, SlashEffectRenderer::new);
        EntityRenderers.register(SBEntityTypes.DRIVE, DriveRenderer::new);
    }

    private static void registerSpecialModelRenderers() {
        var mapper = SpecialModelRenderers.ID_MAPPER;
        mapper.put(BladeBaseRenderer.Unbaked.ID, BladeBaseRenderer.Unbaked.MAP_CODEC);
        mapper.put(BladeNoDataRenderer.Unbaked.ID, BladeNoDataRenderer.Unbaked.MAP_CODEC);
        mapper.put(BladeFPRenderer.LeftHand.Unbaked.ID, BladeFPRenderer.LeftHand.Unbaked.MAP_CODEC);
        mapper.put(BladeFPRenderer.RightHand.Unbaked.ID, BladeFPRenderer.RightHand.Unbaked.MAP_CODEC);
        mapper.put(BladeGroundRenderer.Unbaked.ID, BladeGroundRenderer.Unbaked.MAP_CODEC);
        mapper.put(BladeFixedRenderer.Unbaked.ID, BladeFixedRenderer.Unbaked.MAP_CODEC);
        mapper.put(BladeGuiRenderer.Unbaked.ID, BladeGuiRenderer.Unbaked.MAP_CODEC);
    }
}
