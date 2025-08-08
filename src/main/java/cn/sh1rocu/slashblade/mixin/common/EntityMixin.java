package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {
    @Shadow
    @Nullable
    public abstract String getEncodeId();

    @Shadow
    public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);

    @Shadow
    public abstract void load(CompoundTag nbt);

    @Unique
    private Collection<ItemEntity> sb$captureDrops = null;
    
    @WrapWithCondition(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    public boolean sb$captureDrops(Level level, Entity entity) {
        if (sb$captureDrops != null && entity instanceof ItemEntity item) {
            sb$captureDrops.add(item);
            return false;
        }
        return true;
    }

    @Unique
    @Override
    public Collection<ItemEntity> sb$captureDrops() {
        return sb$captureDrops;
    }

    @Unique
    @Override
    public Collection<ItemEntity> sb$captureDrops(Collection<ItemEntity> value) {
        Collection<ItemEntity> ret = sb$captureDrops;
        sb$captureDrops = value;
        return ret;
    }

    @Unique
    @Override
    public CompoundTag sb$serializeNBT() {
        CompoundTag ret = new CompoundTag();
        String id = getEncodeId();
        if (id != null) {
            ret.putString("id", id);
        }
        return saveWithoutId(ret);
    }

    @Unique
    @Override
    public void sb$deserializeNBT(CompoundTag nbt) {
        load(nbt);
    }

    @Unique
    private CompoundTag sb$persistentData;

    @Unique
    @Override
    public CompoundTag sb$getPersistentData() {
        if (this.sb$persistentData == null) {
            this.sb$persistentData = new CompoundTag();
        }
        return sb$persistentData;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void sb$savePersistentData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.sb$persistentData != null) {
            nbt.put("ForgeData", this.sb$persistentData.copy());
        }
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void save(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (sb$persistentData != null) {
            nbt.put("ForgeData", sb$persistentData);
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void sb$loadPersistentData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("ForgeData", 10)) {
            sb$persistentData = nbt.getCompound("ForgeData");
        }
    }
}