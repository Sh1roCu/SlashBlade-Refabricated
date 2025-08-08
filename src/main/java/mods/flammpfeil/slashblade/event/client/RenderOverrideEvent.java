package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class RenderOverrideEvent extends BaseEvent implements ICancellableEvent {
    ItemStack stack;
    WavefrontObject model;
    String target;
    ResourceLocation texture;

    PoseStack matrixStack;
    MultiBufferSource buffer;

    WavefrontObject originalModel;
    String originalTarget;
    ResourceLocation originalTexture;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onRenderOverride(event);
        }
    });

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public ResourceLocation getOriginalTexture() {
        return originalTexture;
    }

    public WavefrontObject getOriginalModel() {
        return originalModel;
    }

    public String getOriginalTarget() {
        return originalTarget;
    }

    public ItemStack getStack() {
        return stack;
    }

    public WavefrontObject getModel() {
        return model;
    }

    public void setModel(WavefrontObject model) {
        this.model = model;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public PoseStack getPoseStack() {
        return matrixStack;
    }

    public MultiBufferSource getBuffer() {
        return buffer;
    }

    public interface Callback {
        void onRenderOverride(RenderOverrideEvent event);
    }

    public RenderOverrideEvent(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture,
                               PoseStack matrixStack, MultiBufferSource buffer) {
        this.stack = stack;
        this.originalModel = this.model = model;
        this.originalTarget = this.target = target;
        this.originalTexture = this.texture = texture;

        this.matrixStack = matrixStack;
        this.buffer = buffer;
    }

    public static RenderOverrideEvent onRenderOverride(ItemStack stack, WavefrontObject model, String target,
                                                       ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer) {
        RenderOverrideEvent event = new RenderOverrideEvent(stack, model, target, texture, matrixStack, buffer);
        CALLBACK.invoker().onRenderOverride(event);
        return event;
    }
}
