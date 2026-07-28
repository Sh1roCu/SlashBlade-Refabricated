package cn.sh1rocu.slashblade.api.mixin;

public interface IEntityRenderStatePartialTick {
    default float sb$partialTick() {
        throw new AssertionError("Implemented in Mixin");
    }

    default void sb$setPartialTick(float partialTicks){
        throw new AssertionError("Implemented in Mixin");
    };
}
