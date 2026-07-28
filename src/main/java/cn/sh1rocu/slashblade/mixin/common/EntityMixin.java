package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
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
}