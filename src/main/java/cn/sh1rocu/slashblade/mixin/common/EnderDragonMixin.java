package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.util.CommonHooks;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {
    protected EnderDragonMixin(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Unique
    private @Nullable EntityReference<Player> sb$unlimitedLastHurtByPlayer = null;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void sb$storeUnlimitedLastHurtByPlayer(CallbackInfo ci) {
        if (this.lastHurtByPlayer != null) this.sb$unlimitedLastHurtByPlayer = lastHurtByPlayer;
        if (this.sb$unlimitedLastHurtByPlayer != null) {
            Player p = this.sb$unlimitedLastHurtByPlayer.getEntity(this.level(), Player.class);
            if (p == null || p.isRemoved()) {
                this.sb$unlimitedLastHurtByPlayer = null;
            }
        }
    }

    @ModifyArg(
            method = "tickDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
            ),
            index = 2)
    private int sb$modifyExp(int original) {
        return CommonHooks.getExperienceDrop(
                this,
                EntityReference.get(this.sb$unlimitedLastHurtByPlayer, this.level(), Player.class),
                original);
    }
}
