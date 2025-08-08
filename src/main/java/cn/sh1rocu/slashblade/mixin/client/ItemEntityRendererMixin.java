package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.extension.ItemEntityRendererExtension;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// From Kilt
@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin implements ItemEntityRendererExtension {
    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(Lnet/minecraft/world/item/ItemDisplayContext;)Lnet/minecraft/client/renderer/block/model/ItemTransform;", ordinal = 0), ordinal = 3)
    private float sb$onlyBobIfRequired(float original) {
        if (sb$shouldBob())
            return original;
        else
            return 0f;
    }

    @ModifyArgs(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 2))
    private void sb$onlyTranslateIfSpread(Args args) {
        if (!sb$shouldSpreadItems()) {
            args.set(0, 0f);
            args.set(1, 0f);
            args.set(2, 0f);
        }
    }

    @ModifyArgs(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 3))
    private void sb$onlyTranslateIfSprea2(Args args) {
        if (!sb$shouldSpreadItems()) {
            args.set(0, 0f);
            args.set(1, 0f);
        }
    }

    @Override
    public boolean sb$shouldSpreadItems() {
        return true;
    }

    @Override
    public boolean sb$shouldBob() {
        return true;
    }
}