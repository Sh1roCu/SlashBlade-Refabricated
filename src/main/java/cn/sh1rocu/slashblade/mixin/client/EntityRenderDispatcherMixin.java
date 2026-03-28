package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.event.EntityAddedLayerCallback;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow
    private Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers;

    @Shadow
    @Final
    private EntityModelSet entityModels;

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    public void sb$resourceReload(ResourceManager resourceManager, CallbackInfo ci, @Local EntityRendererProvider.Context context) {
        EntityAddedLayerCallback.EVENT.invoker().addLayers(renderers, playerRenderers, context, this.entityModels);
    }
}