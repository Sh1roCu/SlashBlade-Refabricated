package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.*;
import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import cn.sh1rocu.slashblade.util.EventHooks;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtension {
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double sb$modifyKnockbackStrength(double strength, double ogstrength, double xRatio, double zRatio, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        LivingKnockBackEvent event = new LivingKnockBackEvent((LivingEntity) (Object) this, (float) strength, xRatio, zRatio);
        LivingKnockBackEvent.CALLBACK.invoker().onLivingKnockBack(event);
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

    @Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"), cancellable = true)
    private void sb$shouldCancelKnockback(double strength, double xRatio, double zRatio, CallbackInfo ci, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        if (eventRef.get().isCanceled())
            ci.cancel();
    }

    @Unique
    private LivingFallEvent sb$currentFallEvent = null;

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void sb$cancelFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        sb$currentFallEvent = new LivingFallEvent((LivingEntity) (Object) this, fallDistance, multiplier);
        LivingFallEvent.CALLBACK.invoker().onLivingFall(sb$currentFallEvent);
        if (sb$currentFallEvent.isCanceled()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    public float sb$modifyDistance(float fallDistance) {
        if (sb$currentFallEvent != null) {
            return sb$currentFallEvent.getDistance();
        }
        return fallDistance;
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    public float sb$modifyMultiplier(float multiplier) {
        if (sb$currentFallEvent != null) {
            return sb$currentFallEvent.getDamageMultiplier();
        }
        return multiplier;
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void sb$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player)) {
            LivingAttackEvent event = new LivingAttackEvent(self, source, amount);
            LivingAttackEvent.CALLBACK.invoker().onLivingAttack(event);
            if (event.isCanceled())
                cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"), cancellable = true)
    private void sb$tickEvent(CallbackInfo ci) {
        LivingTickEvent event = new LivingTickEvent((LivingEntity) (Object) this);
        LivingTickEvent.CALLBACK.invoker().onLivingTick(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private int sb$expDropEvent(int original) {
        return EventHooks.getExperienceDrop((LivingEntity) (Object) this, this.lastHurtByPlayer, original);
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), index = 2)
    private float sb$livingHurtEvent(float value, DamageSource pDamageSource, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
        LivingHurtEvent event = new LivingHurtEvent((LivingEntity) (Object) this, pDamageSource, value);
        eventRef.set(event);
        LivingHurtEvent.CALLBACK.invoker().onLivingHurt(event);
        if (event.isCanceled())
            return 0;
        return event.getAmount();
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
    private void sb$shouldCancelHurt(DamageSource damageSource, float f, CallbackInfo ci, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
        if (eventRef.get().getAmount() <= 0)
            ci.cancel();
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    public void sb$onJump(CallbackInfo ci) {
        LivingJumpEvent.CALLBACK.invoker().onLivingEntityJump(new LivingJumpEvent((LivingEntity) (Object) this));
    }

    private int sb$lootingLevel;

    @ModifyVariable(
            method = "dropAllDeathLoot",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LivingEntity;lastHurtByPlayerTime:I"
            )
    )
    private int sb$grabLootingLevel(int lootingLevel) {
        sb$lootingLevel = lootingLevel;
        return lootingLevel;
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void sb$startCapturingDrops(DamageSource damageSource, CallbackInfo ci) {
        sb$captureDrops(new ArrayList<>());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void sb$dropCapturedDrops(DamageSource source, CallbackInfo ci) {
        Collection<ItemEntity> drops = this.sb$captureDrops(null);
        LivingDropsEvent event = new LivingDropsEvent((LivingEntity) (Object) this, source, drops, sb$lootingLevel, lastHurtByPlayerTime > 0);
        LivingDropsEvent.CALLBACK.invoker().onLivingDrops(event);
        if (!event.isCanceled())
            drops.forEach(e -> level().addFreshEntity(e));
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void sb$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
        ItemStack stack = getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemSlashBladeExtension blade) {
            if (blade.onEntitySwing(stack, (LivingEntity) (Object) this))
                ci.cancel();
        }
    }
}