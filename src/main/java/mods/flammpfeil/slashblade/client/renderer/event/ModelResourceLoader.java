package mods.flammpfeil.slashblade.client.renderer.event;

import com.google.common.cache.LoadingCache;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.init.DefaultResources;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// 模型资源预加载（防止启动游戏就直接爆玩家显存，只给了原版用）
public class ModelResourceLoader implements PreparableReloadListener {
    private static final Identifier MODEL_DIR = SlashBlade.prefix("model");
    private static final String FILE_TYPES = ".obj";

    private void loadResources(ResourceManager manager) {
        BladeModelManager instance = BladeModelManager.getInstance();
        LoadingCache<Identifier, WavefrontObject> cache = instance.cache;
        cache.invalidateAll();
        instance.defaultModel = new WavefrontObject(DefaultResources.resourceDefaultModel);

        Map<Identifier, Resource> resources = manager.listResources(
                MODEL_DIR.getPath(),
                resLoc -> resLoc.getPath().endsWith(FILE_TYPES)
        );

        resources.keySet().forEach(instance::getModel);
    }

    public static final Identifier ID = SlashBlade.prefix("model_loader");

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor) {
        return CompletableFuture.runAsync(() -> {
            loadResources(currentReload.resourceManager());
        }, reloadExecutor).thenCompose(preparationBarrier::wait);
    }
}