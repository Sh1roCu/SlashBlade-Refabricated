package mods.flammpfeil.slashblade.mixin.client.compat.ar;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.CulledMeshCollector;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.ExtensionMethod;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.GroupObject;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@ExtensionMethod(VertexConsumerExtension.class)
@Mixin(GroupObject.class)
public class AcceleratedGroupObject implements IAcceleratedRenderer<Void> {

    @Shadow(remap = false)
    public List<Face> faces;

    @Unique
    private final Map<IBufferGraph, IMesh> meshes = new Object2ObjectOpenHashMap<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderFaces(VertexConsumer tessellator, PoseStack matrixStack, int light, int color, CallbackInfo ci) {
        var extension = tessellator.getAccelerated();
        if (AcceleratedEntityRenderingFeature.isEnabled()
                && AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()
                && (CoreFeature.isRenderingLevel()
                || (CoreFeature.isRenderingGui() && AcceleratedEntityRenderingFeature.shouldAccelerateInGui()))
                && extension.isAccelerated()) {
            ci.cancel();

            if (faces.size() > 0) {
                extension.doRender(this, null,
                        matrixStack.last().pose(),
                        matrixStack.last().normal(),
                        light,
                        OverlayTexture.NO_OVERLAY,
                        color
                );

            }
        }
    }

    @Override
    public void render(
            VertexConsumer vertexConsumer,
            Void context,
            Matrix4f transform,
            Matrix3f normal,
            int light,
            int overlay,
            int color
    ) {

        var extension = vertexConsumer.getAccelerated();
        var mesh = meshes.get(extension);

        extension.beginTransform(transform, normal);

        if (mesh != null) {
            mesh.write(
                    extension,
                    color,
                    light,
                    overlay
            );

            extension.endTransform();
            return;
        }


        var culledMeshCollector = new CulledMeshCollector(extension);
        var meshBuilder = extension.decorate(culledMeshCollector);

        for (var face : faces) {
            face.addFaceForRender(meshBuilder, 0.0005F, new Matrix4f(), light, -1);
        }

        culledMeshCollector.flush();

        mesh = AcceleratedEntityRenderingFeature
                .getMeshType()
                .getBuilder()
                .build(culledMeshCollector);

        meshes.put(extension, mesh);
        mesh.write(
                extension,
                color,
                light,
                overlay
        );

        extension.endTransform();

    }

}