package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.MobSpawnEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void sb$onFinalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        MobSpawnEvent.FinalizeSpawn event = new MobSpawnEvent.FinalizeSpawn(
                (Mob) (Object) this, serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag
        );
        MobSpawnEvent.FINALIZE_SPAWN.invoker().onFinalizeSpawn(event);
        if (event.isCanceled()) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
