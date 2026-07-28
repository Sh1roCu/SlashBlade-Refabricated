package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class RenderOverrideEvent extends BaseEvent implements ICancellableEvent {
    BladeItemRenderState state;
    WavefrontObject model;
    String target;
    Identifier texture;

    PoseStack matrixStack;
    SubmitNodeCollector submitNodeCollector;

    WavefrontObject originalModel;
    String originalTarget;
    Identifier originalTexture;

    int packedLightIn;
    Function<Identifier, RenderType> getRenderType;
    boolean enableEffect;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onRenderOverride(event);
        }
    });

    public Identifier getTexture() {
        return texture;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public Identifier getOriginalTexture() {
        return originalTexture;
    }

    public WavefrontObject getOriginalModel() {
        return originalModel;
    }

    public String getOriginalTarget() {
        return originalTarget;
    }

    public BladeItemRenderState getRenderState() {
        return state;
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

    public SubmitNodeCollector getSubmitNodeCollector() {
        return submitNodeCollector;
    }

    public int getPackedLightIn() {
        return packedLightIn;
    }

    public void setPackedLightIn(int packedLightIn) {
        this.packedLightIn = packedLightIn;
    }

    public Function<Identifier, RenderType> getGetRenderType() {
        return getRenderType;
    }

    public void setGetRenderType(Function<Identifier, RenderType> getRenderType) {
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

    public RenderOverrideEvent(BladeItemRenderState state, WavefrontObject model, String target, Identifier texture,
                               PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, Function<Identifier, RenderType> getRenderType, boolean enableEffect) {
        this.state = state;
        this.originalModel = this.model = model;
        this.originalTarget = this.target = target;
        this.originalTexture = this.texture = texture;

        this.matrixStack = matrixStack;
        this.submitNodeCollector = submitNodeCollector;
        this.packedLightIn = packedLightIn;
        this.getRenderType = getRenderType;
        this.enableEffect = enableEffect;
    }

    public static RenderOverrideEvent onRenderOverride(BladeItemRenderState state, WavefrontObject model, String target,
                                                       Identifier texture, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, Function<Identifier, RenderType> getRenderType, boolean enableEffect) {
        RenderOverrideEvent event = new RenderOverrideEvent(state, model, target, texture, matrixStack, submitNodeCollector, packedLightIn, getRenderType, enableEffect);
        CALLBACK.invoker().onRenderOverride(event);
        return event;
    }
}
