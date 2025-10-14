package cn.sh1rocu.slashblade.mixin.accessor;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Accessor("isChangingDimension")
    void sb$setChangingDimension(boolean isChangingDimension);
}