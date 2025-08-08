package mods.flammpfeil.slashblade.client.renderer.util;

import com.mojang.blaze3d.vertex.PoseStack;

public class MSAutoCloser implements AutoCloseable {

    public static MSAutoCloser pushMatrix(PoseStack ms) {
        return new MSAutoCloser(ms);
    }

    PoseStack ms;

    MSAutoCloser(PoseStack ms) {
        this.ms = ms;
        this.ms.pushPose();
    }

    @Override
    public void close() {
        this.ms.popPose();
    }
}
