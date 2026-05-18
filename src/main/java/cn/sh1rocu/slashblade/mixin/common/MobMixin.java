package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void sb$onFinalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        MobSpawnEvent.FinalizeSpawn event = new MobSpawnEvent.FinalizeSpawn(
                (Mob) (Object) this, serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData
        );
        MobSpawnEvent.FINALIZE_SPAWN.invoker().onFinalizeSpawn(event);
        if (event.isCanceled()) {
            cir.setReturnValue(null);
        } else {
            cir.setReturnValue(event.getSpawnData());
        }
    }

    @WrapOperation(method = "getApproximateAttackDamageWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object sb$getDefaultAttributeModifiers(ItemStack instance, DataComponentType dataComponentType, Object object, Operation<Object> original) {
        if (instance.getItem() instanceof ItemSlashBladeExtension blade) {
            return blade.getDefaultAttributeModifiers(instance);
        }
        return original.call(instance, dataComponentType, object);
    }
}
