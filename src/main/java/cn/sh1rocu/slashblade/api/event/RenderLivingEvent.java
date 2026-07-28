package cn.sh1rocu.slashblade.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderLivingEvent<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends BaseEvent {
    private final S renderState;
    private final LivingEntityRenderer<T, S, M> renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final SubmitNodeCollector submitNodeCollector;

    @SuppressWarnings({"rawtypes","unchecked"})
    public static final Event<Pre.Callback> PRE = EventFactory.createArrayBacked(Pre.Callback.class, callbacks -> event -> {
        for (Pre.Callback callback : callbacks) {
            callback.post(event);
        }
    });
    @SuppressWarnings({"rawtypes","unchecked"})
    public static final Event<Post.Callback> POST = EventFactory.createArrayBacked(Post.Callback.class, callbacks -> event -> {
        for (Post.Callback callback : callbacks) {
            callback.post(event);
        }
    });

    protected RenderLivingEvent(S renderState, LivingEntityRenderer<T, S, M> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        this.renderState = renderState;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.submitNodeCollector = submitNodeCollector;
    }

    public S getRenderState() {
        return renderState;
    }

    public LivingEntityRenderer<T, S, M> getRenderer() {
        return renderer;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public SubmitNodeCollector getSubmitNodeCollector() {
        return submitNodeCollector;
    }

    public static class Pre<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLivingEvent<T, S, M> implements ICancellableEvent {
        public Pre(S renderState, LivingEntityRenderer<T, S, M> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
            super(renderState, renderer, partialTick, poseStack, submitNodeCollector);
        }

        public interface Callback<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
            void post(Pre<T, S, M> event);
        }
    }

    public static class Post<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLivingEvent<T, S, M> {
        public Post(S renderState, LivingEntityRenderer<T, S, M> renderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
            super(renderState, renderer, partialTick, poseStack, submitNodeCollector);
        }

        public interface Callback<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
            void post(Post<T, S, M> event);
        }
    }
}
