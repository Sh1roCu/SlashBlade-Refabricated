package mods.flammpfeil.slashblade.client.renderer.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    public String name;
    public List<Face> faces = new ArrayList<>();
    public int glDrawingMode;

    public GroupObject() {
        this("");
    }

    public GroupObject(String name) {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @Environment(EnvType.CLIENT)
    public void render(VertexConsumer tessellator, PoseStack matrixStack, int light, int color) {
        if (faces.size() > 0) {
            for (Face face : faces) {
                face.addFaceForRender(tessellator, matrixStack, light, color);
            }
        }
    }
}