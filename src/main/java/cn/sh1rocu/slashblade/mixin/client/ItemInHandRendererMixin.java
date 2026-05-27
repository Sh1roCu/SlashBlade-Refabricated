package cn.sh1rocu.slashblade.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(
            method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            index = 5,
            argsOnly = true
    )
    private float sb$removeVanillaSwingForSlashBlade(float swingProgress, AbstractClientPlayer player,
                                                     float partialTicks, float pitch, InteractionHand hand,
                                                     float inSwingProgress, ItemStack stack) {
        if (hand != InteractionHand.MAIN_HAND) {
            return swingProgress;
        }

        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return swingProgress;
        }

        if (this.minecraft == null || this.minecraft.player == null) {
            return swingProgress;
        }

        if (player != this.minecraft.player) {
            return swingProgress;
        }

        return 0.0F;
    }
}
