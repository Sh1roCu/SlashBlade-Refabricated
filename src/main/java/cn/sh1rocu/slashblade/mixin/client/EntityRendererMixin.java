package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void sb$extractRenderState(final T entity, final S state, final float partialTicks, CallbackInfo ci) {
        state.sb$setPartialTick(partialTicks);
        state.setData(RenderStateKeys.ENTITY_ID, entity.getId());
        state.setData(RenderStateKeys.TICK_COUNT, entity.tickCount);
    }
}