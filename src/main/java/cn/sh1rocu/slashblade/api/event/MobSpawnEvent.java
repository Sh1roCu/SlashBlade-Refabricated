package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class MobSpawnEvent extends BaseEvent {
    private final Mob mob;
    private final ServerLevelAccessor level;
    public static final Event<FinalizeSpawnCallback> FINALIZE_SPAWN = EventFactory.createArrayBacked(FinalizeSpawnCallback.class, callbacks -> event -> {
        for (FinalizeSpawnCallback callback : callbacks) {
            callback.onFinalizeSpawn(event);
        }
    });

    public interface FinalizeSpawnCallback {
        void onFinalizeSpawn(FinalizeSpawn event);
    }

    protected MobSpawnEvent(Mob mob, ServerLevelAccessor level) {
        this.mob = mob;
        this.level = level;
    }

    public Mob getEntity() {
        return this.mob;
    }

    public ServerLevelAccessor getLevel() {
        return this.level;
    }

    public static class FinalizeSpawn extends MobSpawnEvent implements ICancellableEvent {
        private final MobSpawnType spawnType;
        private DifficultyInstance difficulty;
        @Nullable
        private SpawnGroupData spawnData;
        @Nullable
        private CompoundTag spawnTag;

        @ApiStatus.Internal
        public FinalizeSpawn(Mob entity, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
            super(entity, level);
            this.difficulty = difficulty;
            this.spawnType = spawnType;
            this.spawnData = spawnData;
            this.spawnTag = spawnTag;
        }

        public DifficultyInstance getDifficulty() {
            return this.difficulty;
        }

        public void setDifficulty(DifficultyInstance inst) {
            this.difficulty = inst;
        }

        public MobSpawnType getSpawnType() {
            return this.spawnType;
        }

        @Nullable
        public SpawnGroupData getSpawnData() {
            return this.spawnData;
        }

        public void setSpawnData(@Nullable SpawnGroupData data) {
            this.spawnData = data;
        }

        @Nullable
        public CompoundTag getSpawnTag() {
            return this.spawnTag;
        }

        public void setSpawnTag(@Nullable CompoundTag tag) {
            this.spawnTag = tag;
        }
    }
}