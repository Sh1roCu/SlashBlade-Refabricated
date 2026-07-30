package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {
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

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
    private void sb$savePersistentData(ValueOutput output, CallbackInfo ci) {
        output.storeNullable("NeoForgeData", CompoundTag.CODEC, this.sb$persistentData);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"))
    private void sb$loadPersistentData(ValueInput input, CallbackInfo ci) {
        input.read("NeoForgeData", CompoundTag.CODEC).ifPresent((neoData) -> this.sb$persistentData = neoData);
    }

    @WrapOperation(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"
            )
    )
    @SuppressWarnings("ConstantConditions")
    private boolean sb$allowModProjectileToRide(EntityType<?> instance, Operation<Boolean> original) {
        // 也许不止玩家实体类型在构建时调用了noSave（即canSerialize为false）
        // 因此这里暂时注释掉玩家类型判断，以防不兼容其他实体
        if (/*instance == EntityType.PLAYER && */(Entity) (Object) this instanceof mods.flammpfeil.slashblade.entity.Projectile) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}