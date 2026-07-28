package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import cn.sh1rocu.slashblade.api.event.RenderLivingEvent;
import cn.sh1rocu.slashblade.api.event.RenderPlayerEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mods.flammpfeil.slashblade.event.client.UserPoseOverrider.TAG_ROT;
import static mods.flammpfeil.slashblade.event.client.UserPoseOverrider.TAG_ROT_PREV;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @SuppressWarnings("unchecked,rawtypes")
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void sb$onPreRender(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        var renderer = (LivingEntityRenderer) (Object) this;
        if (state instanceof AvatarRenderState avatar && renderer instanceof AvatarRenderer avatarRenderer) {
            var event = new RenderPlayerEvent.Pre<>(avatar, avatarRenderer,
                    avatar.sb$partialTick(), poseStack, submitNodeCollector);
            RenderPlayerEvent.PRE.invoker().post(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        } else {
            var event = new RenderLivingEvent.Pre<>(state, renderer,
                    state.sb$partialTick(), poseStack, submitNodeCollector);
            RenderLivingEvent.PRE.invoker().post(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    private void sb$onPostRender(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        var event = new RenderLivingEvent.Post<>(state, (LivingEntityRenderer<T, S, M>) (Object) this,
                state.sb$partialTick(), poseStack, submitNodeCollector);
        RenderLivingEvent.POST.invoker().post(event);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("HEAD"))
    private void sb$extractRenderState(T entity, S state, float partialTicks, CallbackInfo ci) {
        float rot = ((EntityExtension) entity).sb$getPersistentData().getFloatOr(TAG_ROT, 0);
        float rotPrev = ((EntityExtension) entity).sb$getPersistentData().getFloatOr(TAG_ROT_PREV, 0);
        int fallFlyingTicks = entity.getFallFlyingTicks();
        Vec3 viewVector = entity.getViewVector(partialTicks);
        Vec3 deltaMovement = entity.getDeltaMovement();
        state.setData(RenderStateKeys.PERSISTENT_DATA_YROT, rot);
        state.setData(RenderStateKeys.PERSISTENT_DATA_PREV_YROT, rotPrev);
        state.setData(RenderStateKeys.POSITION, entity.getPosition(partialTicks));
        state.setData(RenderStateKeys.IS_ALIVE, entity.isAlive());
        state.setData(RenderStateKeys.HP, entity.getHealth());
        state.setData(RenderStateKeys.MAX_HP, entity.getMaxHealth());
        state.setData(RenderStateKeys.BB_HEIGHT, entity.getBbHeight());
        state.setData(RenderStateKeys.EXTRA_ENTITY_RENDER_DATA, new RenderStateKeys.ExtraEntityRenderData(fallFlyingTicks, viewVector, deltaMovement));

        CapabilitySlashBlade.getBladeState(entity.getMainHandItem()).ifPresent(s -> {
            ComboState combo = ComboStateRegistry.COMBO_STATE.get(s.getComboSeq()).isPresent()
                    ? ComboStateRegistry.COMBO_STATE.getValue(s.getComboSeq())
                    : ComboStateRegistry.NONE;
            // tick to msec
            double time = TimeValueHelper.getMSecFromTicks(
                    Math.max(0, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks);

            while (combo != ComboStateRegistry.NONE && combo.getTimeoutMS() < time) {
                time -= combo.getTimeoutMS();

                combo = ComboStateRegistry.COMBO_STATE.get(combo.getNextOfTimeout(entity)).isPresent()
                        ? ComboStateRegistry.COMBO_STATE.getValue(combo.getNextOfTimeout(entity))
                        : ComboStateRegistry.NONE;
            }
            if (combo == ComboStateRegistry.NONE) {
                combo = ComboStateRegistry.COMBO_STATE.get(s.getComboRoot()).isPresent()
                        ? ComboStateRegistry.COMBO_STATE.getValue(s.getComboRoot())
                        : ComboStateRegistry.STANDBY;
            }
            state.setData(RenderStateKeys.COMBO_STATE_TIME, time);
            state.setData(RenderStateKeys.COMBO_STATE, combo);

            state.setData(RenderStateKeys.BLADE_IS_CHARGED, s.isCharged(entity));
        });
    }
}