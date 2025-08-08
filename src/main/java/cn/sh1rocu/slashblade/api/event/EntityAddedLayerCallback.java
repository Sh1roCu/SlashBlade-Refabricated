package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

// Porting Lib
@FunctionalInterface
public interface EntityAddedLayerCallback {

    Event<EntityAddedLayerCallback> EVENT = EventFactory.createArrayBacked(EntityAddedLayerCallback.class, callbacks -> (renderers, skinMap) -> {
        for (EntityAddedLayerCallback event : callbacks) {
            event.addLayers(renderers, skinMap);
        }
    });

    void addLayers(final Map<EntityType<?>, EntityRenderer<?>> renderers, final Map<String, EntityRenderer<? extends Player>> skinMap);

}