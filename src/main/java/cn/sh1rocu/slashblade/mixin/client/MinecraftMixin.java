package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.event.RenderFrameEvent;
import cn.sh1rocu.slashblade.util.ClientHooks;
import mods.flammpfeil.slashblade.event.handler.BlockPickCanceller;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
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
    @Final
    private DeltaTracker.Timer timer;

    @Shadow
    @Final
    public Options options;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void sb$onRenderStart(CallbackInfo ci) {
        RenderFrameEvent.START.invoker().onStart(new RenderFrameEvent.Pre(this.timer));
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V", shift = At.Shift.AFTER))
    private void sb$onRenderEnd(CallbackInfo ci) {
        RenderFrameEvent.END.invoker().onEnd(new RenderFrameEvent.Post(this.timer));
    }

    @Inject(method = "pickBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", ordinal = 0), cancellable = true)
    private void sb$inputClickEvent(CallbackInfo ci) {
        AtomicBoolean canceled = new AtomicBoolean(false);
        BlockPickCanceller.onBlockPick(canceled);
        if (canceled.get())
            ci.cancel();
    }
}