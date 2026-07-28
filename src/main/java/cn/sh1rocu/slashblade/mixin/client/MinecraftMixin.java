package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.event.RenderFrameEvent;
import mods.flammpfeil.slashblade.event.handler.BlockPickCanceller;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    private DeltaTracker.Timer deltaTracker;

    @Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void sb$onRenderStart(boolean advanceGameTime, CallbackInfo ci) {
        RenderFrameEvent.START.invoker().onStart(new RenderFrameEvent.Pre(this.deltaTracker));
    }

    @Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V", shift = At.Shift.AFTER))
    private void sb$onRenderEnd(CallbackInfo ci) {
        RenderFrameEvent.END.invoker().onEnd(new RenderFrameEvent.Post(this.deltaTracker));
    }

    @Inject(method = "pickBlockOrEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z"), cancellable = true)
    private void sb$inputClickEvent(CallbackInfo ci) {
        AtomicBoolean canceled = new AtomicBoolean(false);
        BlockPickCanceller.onBlockPick(canceled);
        if (canceled.get())
            ci.cancel();
    }
}