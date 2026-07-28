package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.IEntityRepresentation;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity {
    @Shadow
    @Final
    private static EntityDataAccessor<ItemStack> DATA_ITEM;

    @Shadow
    public abstract ItemStack getItem();

    protected ItemFrameMixin(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_ITEM.equals(accessor)) {
            if (!((ItemFrame) (Object) this instanceof BladeStandEntity)) {
                return;
            }
            ItemStack stack = this.getItem();
            if (stack.getItem() instanceof ItemSlashBlade) {
                ((IEntityRepresentation) (Object) stack).sb$setEntityRepresentation(this);
            }
        }
    }

    @Inject(method = "removeFramedMap", at = @At("TAIL"))
    private void sb$removeFrame(ItemStack itemStack, CallbackInfo ci) {
        ((IEntityRepresentation) (Object) itemStack).sb$setEntityRepresentation(null);
    }
}
