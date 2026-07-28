package cn.sh1rocu.slashblade.api.extension;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;

import javax.annotation.Nullable;

public interface IEntityRepresentation {
    void sb$setEntityRepresentation(@Nullable Entity entity);

    @Nullable
    Entity sb$getEntityRepresentation();

    @Nullable
    ItemFrame sb$getFrame();
}
