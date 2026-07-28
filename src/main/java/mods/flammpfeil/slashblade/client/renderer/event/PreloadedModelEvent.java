package mods.flammpfeil.slashblade.client.renderer.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

// 预加载模型的事件
@Environment(EnvType.CLIENT)
public class PreloadedModelEvent {
    public static void registerResourceLoaders() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ModelResourceLoader.ID, new ModelResourceLoader());
    }
}