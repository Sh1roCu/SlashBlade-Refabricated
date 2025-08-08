package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.api.event.LivingAttackEvent;
import cn.sh1rocu.slashblade.api.event.PlayerFlyableFallEvent;
import cn.sh1rocu.slashblade.api.event.PlayerTickEvent;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "causeFallDamage", at = @At(value = "RETURN", ordinal = 0))
    private void sb$onFlyableFallEvent(float fallDistance, float multiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerFlyableFallEvent.CALLBACK.invoker().onPlayerFlyableFall(new PlayerFlyableFallEvent(
                (Player) (Object) this, fallDistance, multiplier
        ));
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void sb$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingAttackEvent event = new LivingAttackEvent(this, source, amount);
        LivingAttackEvent.CALLBACK.invoker().onLivingAttack(event);
        if (event.isCanceled())
            cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void sb$tickStartEvent(CallbackInfo ci) {
        PlayerTickEvent.START.invoker().onStart(new PlayerTickEvent.Pre((Player) (Object) this));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void sb$tickEndEvent(CallbackInfo ci) {
        PlayerTickEvent.END.invoker().onEnd(new PlayerTickEvent.Post((Player) (Object) this));
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder sb$modifyAttributes(AttributeSupplier.Builder original) {
        original.add(ModAttributes.SLASHBLADE_DAMAGE);
        return original;
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void sb$itemAttack(Entity targetEntity, CallbackInfo ci) {
        Item item = getMainHandItem().getItem();
        if ((item instanceof ItemSlashBladeExtension blade)) {
            if (blade.onLeftClickEntity(getMainHandItem(), (Player) (Object) this, targetEntity))
                ci.cancel();
        }
    }
}
