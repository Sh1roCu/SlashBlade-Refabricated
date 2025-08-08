package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.util.EventHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// From Kilt
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {
    @Nullable
    @Unique
    private Player sb$unlimitedLastHurtByPlayer = null;

    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void sb$storeLastHurtByPlayer(CallbackInfo ci) {
        if (this.lastHurtByPlayer != null)
            this.sb$unlimitedLastHurtByPlayer = this.lastHurtByPlayer;

        if (this.sb$unlimitedLastHurtByPlayer != null && this.sb$unlimitedLastHurtByPlayer.isRemoved())
            this.sb$unlimitedLastHurtByPlayer = null;
    }

    @ModifyArg(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private int sb$useForgeModifiedExperienceDrop(int experience) {
        return EventHooks.getExperienceDrop(this, this.sb$unlimitedLastHurtByPlayer, experience);
    }
}