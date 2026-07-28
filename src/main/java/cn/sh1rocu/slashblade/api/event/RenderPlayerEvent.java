/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package cn.sh1rocu.slashblade.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.jspecify.annotations.NonNull;

/**
 * Fired when a player is being rendered.
 * See the two subclasses for listening for before and after rendering.
 *
 * @see RenderPlayerEvent.Pre
 * @see RenderPlayerEvent.Post
 * @see AvatarRenderer
 */
public abstract class RenderPlayerEvent<T extends Avatar & ClientAvatarEntity> extends RenderLivingEvent<T, AvatarRenderState, PlayerModel> {
    public static final Event<Pre.Callback> PRE = EventFactory.createArrayBacked(Pre.Callback.class, callbacks -> event -> {
        for (Pre.Callback callback : callbacks) {
            callback.post(event);
        }
    });
    public static final Event<Post.Callback> POST = EventFactory.createArrayBacked(Post.Callback.class, callbacks -> event -> {
        for (Post.Callback callback : callbacks) {
            callback.post(event);
        }
    });

    protected RenderPlayerEvent(AvatarRenderState renderState, AvatarRenderer<@NonNull T> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        super(renderState, renderer, partialTick, poseStack, submitNodeCollector);
    }

    @Override
    public AvatarRenderer<@NonNull T> getRenderer() {
        return (AvatarRenderer<@NonNull T>) super.getRenderer();
    }

    public static class Pre<T extends Avatar & ClientAvatarEntity> extends RenderPlayerEvent<T> implements ICancellableEvent {
        public Pre(AvatarRenderState renderState, AvatarRenderer<@NonNull T> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
            super(renderState, renderer, partialTick, poseStack, submitNodeCollector);
        }

        @SuppressWarnings("rawtypes")
        public interface Callback {
            void post(Pre event);
        }
    }

    public static class Post<T extends Avatar & ClientAvatarEntity> extends RenderPlayerEvent<T> {
        public Post(AvatarRenderState renderState, AvatarRenderer<@NonNull T> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
            super(renderState, renderer, partialTick, poseStack, submitNodeCollector);
        }

        @SuppressWarnings("rawtypes")
        public interface Callback {
            void post(Post event);
        }
    }
}
