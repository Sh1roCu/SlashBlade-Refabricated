package mods.flammpfeil.slashblade.compat.iris;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.irisshaders.iris.pipeline.IrisPipelines;

import java.util.HashSet;
import java.util.Set;

public final class IrisCompat {
    private static final String IRIS = "iris";
    private static boolean LOADED;

    private static final Set<RenderPipeline> ASSIGNED_PIPELINES = new HashSet<>();

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

    public static void assignPipeline(RenderPipeline pipeline, String program) {
        if (LOADED && !ASSIGNED_PIPELINES.contains(pipeline)) {
            IrisApi.getInstance().assignPipeline(pipeline, IrisProgram.valueOf(program));
            ASSIGNED_PIPELINES.add(pipeline);
        }
    }
}
