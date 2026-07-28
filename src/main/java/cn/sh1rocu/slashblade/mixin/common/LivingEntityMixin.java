package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.LivingKnockBackEvent;
import cn.sh1rocu.slashblade.api.event.LivingTickEvent;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import cn.sh1rocu.slashblade.util.CommonHooks;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mods.flammpfeil.slashblade.event.drop.EntityDropEvent;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtension {
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    @Shadow
    protected int lastHurtByPlayerMemoryTime;

    @Shadow
    public abstract Player getLastHurtByPlayer();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"), cancellable = true)
    private void sb$tickEvent(CallbackInfo ci) {
        LivingTickEvent event = new LivingTickEvent((LivingEntity) (Object) this);
        LivingTickEvent.EVENT.invoker().onLivingTick(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void sb$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
        ItemStack stack = getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemSlashBladeExtension blade) {
            if (blade.onEntitySwing(stack, (LivingEntity) (Object) this))
                ci.cancel();
        }
    }

    @Inject(method = "jumpFromGround", at = @At(
            value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;needsSync:Z",
            shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void sb$jumpFromGround(CallbackInfo ci) {
        CommonHooks.onLivingJump((LivingEntity) (Object) this);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void sb$dropAllDeathLoot(ServerLevel level, DamageSource source, CallbackInfo ci) {
        EntityDropEvent.dropBlade((LivingEntity) (Object) this, source, this.lastHurtByPlayerMemoryTime > 0);
    }

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;causeFallDamage(DFLnet/minecraft/world/damagesource/DamageSource;)Z"))
    private void sb$livingFall(double fallDistance, float damageModifier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        FallHandler.onFall((LivingEntity) (Object) this);
    }

    @ModifyArg(
            method = "dropExperience",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
            ),
            index = 2)
    private int sb$modifyExp(int original) {
        return CommonHooks.getExperienceDrop((LivingEntity) (Object) this, this.getLastHurtByPlayer(), original);
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double sb$modifyKnockbackStrength(double strength, double ogstrength, double xRatio, double zRatio, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        var event = CommonHooks.onLivingKnockBack((LivingEntity) (Object) this, (float) strength, xRatio, zRatio);
        eventRef.set(event);
        if (!event.isCanceled() && event.getOriginalStrength() != event.getStrength()) {
            return event.getStrength();
        }
        return strength;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double sb$modifyRatioX(double ratioX, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        var event = eventRef.get();
        if (event.getOriginalRatioX() != event.getRatioX())
            return event.getRatioX();
        return ratioX;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private double sb$modifyRatioZ(double ratioZ, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        var event = eventRef.get();
        if (event.getOriginalRatioZ() != event.getRatioZ())
            return event.getRatioZ();
        return ratioZ;
    }

    @Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"), cancellable = true)
    private void sb$shouldCancelKnockback(double strength, double xRatio, double zRatio, CallbackInfo ci, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        if (eventRef.get().isCanceled())
            ci.cancel();
    }
}