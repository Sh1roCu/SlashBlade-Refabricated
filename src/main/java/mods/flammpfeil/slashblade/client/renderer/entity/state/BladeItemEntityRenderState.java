package mods.flammpfeil.slashblade.client.renderer.entity.state;

import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public class BladeItemEntityRenderState extends ItemEntityRenderState {
    public @Nullable BladeItemRenderState itemRenderState = null;
    public Identifier modelLocation;
    public Identifier textureLocation;
    public boolean isInWater;
    public boolean onGround;
    public int tickCount;
    public float entityYaw;
}
