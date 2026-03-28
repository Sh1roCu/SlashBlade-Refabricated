package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

// Porting Lib
@FunctionalInterface
public interface EntityAddedLayerCallback {

    Event<EntityAddedLayerCallback> EVENT = EventFactory.createArrayBacked(EntityAddedLayerCallback.class,
            callbacks -> (renderers, skinMap, context, entityModelSet) -> {
                for (EntityAddedLayerCallback event : callbacks) {
                    event.addLayers(renderers, skinMap, context, entityModelSet);
                }
            });

    void addLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap, EntityRendererProvider.Context context, EntityModelSet entityModelSet);

}