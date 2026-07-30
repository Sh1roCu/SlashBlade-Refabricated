package mods.flammpfeil.slashblade.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class BladeStandEntityRenderState extends EntityRenderState {
    public BlockPos blockPos;
    public Vec3 position;
    public float xRot;
    public float yRot;
    public int rotation;
    public Item currentType;
    public final ItemStackRenderState blade;
    public final ItemStackRenderState bladeStand;

    public BladeStandEntityRenderState() {
        this.blade = new ItemStackRenderState();
        this.bladeStand = new ItemStackRenderState();
    }
}
