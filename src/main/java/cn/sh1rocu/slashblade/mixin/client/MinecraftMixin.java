package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.event.RenderTickEvent;
import mods.flammpfeil.slashblade.event.handler.BlockPickCanceller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Timer;
import net.minecraft.client.particle.ParticleEngine;
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
    @Final
    private Timer timer;

    @Shadow
    @Final
    public Options options;

    @Shadow
    @Final
    public ParticleEngine particleEngine;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    private void sb$onRenderStart(CallbackInfo ci) {
        RenderTickEvent.START.invoker().onStart(new RenderTickEvent.Pre(this.timer));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
                    ordinal = 4
            )
    )
    private void sb$onRenderEnd(CallbackInfo ci) {
        RenderTickEvent.END.invoker().onEnd(new RenderTickEvent.Post(this.timer));
    }

    @Inject(method = "pickBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", ordinal = 0), cancellable = true)
    private void sb$inputClickEvent(CallbackInfo ci) {
        AtomicBoolean canceled = new AtomicBoolean(false);
        BlockPickCanceller.onBlockPick(canceled);
        if (canceled.get())
            ci.cancel();
    }
}