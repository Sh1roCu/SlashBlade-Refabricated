package mods.flammpfeil.slashblade.compat.iris;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;

public final class IrisCompat {
    private static final String IRIS = "iris";
    private static boolean LOADED;

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(IRIS)) {
            LOADED = true;
        }
    }

    public static boolean isUsingRenderPack() {
        if (LOADED) {
            return IrisApi.getInstance().isShaderPackInUse();
        }
        return false;
    }

    public static boolean isRenderingShadowPass() {
        if (LOADED) {
            return IrisApi.getInstance().isRenderingShadowPass();
        }
        return false;
    }
}
