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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

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

    int packedLightIn;
    Function<ResourceLocation, RenderType> getRenderType;
    boolean enableEffect;

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

    public int getPackedLightIn() {
        return packedLightIn;
    }

    public void setPackedLightIn(int packedLightIn) {
        this.packedLightIn = packedLightIn;
    }

    public Function<ResourceLocation, RenderType> getGetRenderType() {
        return getRenderType;
    }

    public void setGetRenderType(Function<ResourceLocation, RenderType> getRenderType) {
        this.getRenderType = getRenderType;
    }

    public boolean isEnableEffect() {
        return enableEffect;
    }

    public void setEnableEffect(boolean enableEffect) {
        this.enableEffect = enableEffect;
    }

    public interface Callback {
        void onRenderOverride(RenderOverrideEvent event);
    }

    public RenderOverrideEvent(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture,
                               PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, Function<ResourceLocation, RenderType> getRenderType, boolean enableEffect) {
        this.stack = stack;
        this.originalModel = this.model = model;
        this.originalTarget = this.target = target;
        this.originalTexture = this.texture = texture;

        this.matrixStack = matrixStack;
        this.buffer = buffer;
        this.packedLightIn = packedLightIn;
        this.getRenderType = getRenderType;
        this.enableEffect = enableEffect;
    }

    public static RenderOverrideEvent onRenderOverride(ItemStack stack, WavefrontObject model, String target,
                                                       ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn, Function<ResourceLocation, RenderType> getRenderType, boolean enableEffect) {
        RenderOverrideEvent event = new RenderOverrideEvent(stack, model, target, texture, matrixStack, buffer, packedLightIn, getRenderType, enableEffect);
        CALLBACK.invoker().onRenderOverride(event);
        return event;
    }
}
