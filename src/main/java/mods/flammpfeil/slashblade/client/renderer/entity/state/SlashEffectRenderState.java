package mods.flammpfeil.slashblade.client.renderer.entity.state;

import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SlashEffectRenderState extends EntityRenderState {
    public int lifetime;
    public int tickCount;
    public float yRotO;
    public float yRot;
    public float xRotO;
    public float xRot;
    public int color;
    public float rotationRoll;
    public float rotationOffset;
    public float baseSize;
    public IConcentrationRank.ConcentrationRanks rankCode;
}
