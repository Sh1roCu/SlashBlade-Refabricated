package mods.flammpfeil.slashblade.client.renderer.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.concurrent.Executors;

/**
 * Created by Furia on 2016/02/06.
 */
@Environment(EnvType.CLIENT)
public class BladeModelManager {

    private static final class SingletonHolder {
        private static final BladeModelManager instance = new BladeModelManager();
    }

    public static BladeModelManager getInstance() {
        return SingletonHolder.instance;
    }

    public static Registry<SlashBladeDefinition> getClientSlashBladeRegistry() {
        return Minecraft.getInstance().getConnection().registryAccess().lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY);
    }

    public WavefrontObject defaultModel;

    public LoadingCache<Identifier, WavefrontObject> cache;

    private BladeModelManager() {
        defaultModel = new WavefrontObject(DefaultResources.resourceDefaultModel);

        cache = CacheBuilder.newBuilder()
                .build(CacheLoader.asyncReloading(new CacheLoader<>() {
                    @Override
                    public WavefrontObject load(Identifier key) {
                        try {
                            return new WavefrontObject(key);
                        } catch (Exception e) {
                            return defaultModel;
                        }
                    }

                }, Executors.newCachedThreadPool()));
    }

//    public void reload(TextureAtlas atlas) {
//        cache.invalidateAll();
//
//        defaultModel = new WavefrontObject(DefaultResources.resourceDefaultModel);
//    }

    public WavefrontObject getModel(Identifier loc) {
        if (loc != null) {
            try {
                return cache.get(loc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultModel;
    }

}
