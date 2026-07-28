package mods.flammpfeil.slashblade.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class BladeStandEntityRenderState extends ItemFrameRenderState {
    public BlockPos blockPos;
    public Vec3 position;
    public float xRot;
    public float yRot;
    public int rotation;
    public Item currentType;
}
