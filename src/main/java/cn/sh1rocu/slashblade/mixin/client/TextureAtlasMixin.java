package cn.sh1rocu.slashblade.mixin.client;

import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Inject(method = "upload", at = @At("RETURN"))
    private void sb$postStitch(SpriteLoader.Preparations preparations, CallbackInfo ci) {
        TextureAtlas atlas = (TextureAtlas) (Object) this;
        // BladeModelManager.getInstance().reload(atlas);
        BladeMotionManager.getInstance().reload(atlas);
    }
}