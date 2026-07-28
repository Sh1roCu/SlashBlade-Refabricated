package cn.sh1rocu.slashblade.mixin.client;

import cn.sh1rocu.slashblade.api.mixin.IEntityRenderStatePartialTick;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IEntityRenderStatePartialTick {
    @Unique
    private float sb$partialTick;

    @Override
    public float sb$partialTick() {
        return this.sb$partialTick;
    }

    @Override
    public void sb$setPartialTick(float partialTicks) {
        this.sb$partialTick = partialTicks;
    }
}