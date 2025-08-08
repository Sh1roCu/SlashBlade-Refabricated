package mods.flammpfeil.slashblade.event;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class SlashBladeBaseEvent extends BaseEvent {
    private final ItemStack blade;
    private final ISlashBladeState state;

    public static final Event<Break> BREAK = EventFactory.createArrayBacked(Break.class, callbacks -> event -> {
        for (Break callback : callbacks) {
            callback.onBreak(event);
        }
    });
    public static final Event<PowerBlade> POWER_BLADE = EventFactory.createArrayBacked(PowerBlade.class, callbacks -> event -> {
        for (PowerBlade callback : callbacks) {
            callback.onPower(event);
        }
    });
    public static final Event<UpdateAttack> UPDATE_ATTACK = EventFactory.createArrayBacked(UpdateAttack.class, callbacks -> event -> {
        for (UpdateAttack callback : callbacks) {
            callback.onUpdateAttack(event);
        }
    });
    public static final Event<BladeStandAttack> BLADE_STAND_ATTACK = EventFactory.createArrayBacked(BladeStandAttack.class, callbacks -> event -> {
        for (BladeStandAttack callback : callbacks) {
            callback.onBladeStandAttack(event);
        }
    });
    public static final Event<Hit> HIT = EventFactory.createArrayBacked(Hit.class, callbacks -> event -> {
        for (Hit callback : callbacks) {
            callback.onHit(event);
        }
    });
    public static final Event<Update> UPDATE = EventFactory.createArrayBacked(Update.class, callbacks -> event -> {
        for (Update callback : callbacks) {
            callback.onUpdate(event);
        }
    });
    public static final Event<DoSlash> DO_SLASH = EventFactory.createArrayBacked(DoSlash.class, callbacks -> event -> {
        for (DoSlash callback : callbacks) {
            callback.onDoSlash(event);
        }
    });
    public static final Event<ChargeAction> CHARGE_ACTION = EventFactory.createArrayBacked(ChargeAction.class, callbacks -> event -> {
        for (ChargeAction callback : callbacks) {
            callback.onChargeAction(event);
        }
    });

    public SlashBladeBaseEvent(ItemStack blade, ISlashBladeState state) {
        this.blade = blade;
        this.state = state;
    }

    public ItemStack getBlade() {
        return blade;
    }

    public ISlashBladeState getSlashBladeState() {
        return state;
    }

    public interface Break {
        void onBreak(BreakBaseEvent event);
    }

    public interface PowerBlade {
        void onPower(PowerBladeBaseEvent event);
    }

    public interface UpdateAttack {
        void onUpdateAttack(UpdateAttackBaseEvent event);
    }

    public interface BladeStandAttack {
        void onBladeStandAttack(BladeStandAttackBaseEvent event);
    }

    public interface Hit {
        void onHit(HitBaseEvent event);
    }

    public interface Update {
        void onUpdate(UpdateBaseEvent event);
    }

    public interface DoSlash {
        void onDoSlash(DoSlashBaseEvent event);
    }

    public interface ChargeAction {
        void onChargeAction(ChargeActionBaseEvent event);
    }

    public static class BreakBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
        public BreakBaseEvent(ItemStack blade, ISlashBladeState state) {
            super(blade, state);
        }
    }

    public static class PowerBladeBaseEvent extends SlashBladeBaseEvent {
        private final LivingEntity user;
        private boolean isPowered;

        public PowerBladeBaseEvent(ItemStack blade, ISlashBladeState state, LivingEntity user, boolean isPowered) {
            super(blade, state);
            this.user = user;
            this.setPowered(isPowered);
        }

        public boolean isPowered() {
            return isPowered;
        }

        public void setPowered(boolean isPowered) {
            this.isPowered = isPowered;
        }

        public LivingEntity getUser() {
            return user;
        }

    }

    public static class UpdateAttackBaseEvent extends SlashBladeBaseEvent {
        private final double originDamage;
        private double newDamage;

        public UpdateAttackBaseEvent(ItemStack blade, ISlashBladeState state, double damage) {
            super(blade, state);
            this.originDamage = damage;
            this.newDamage = damage;
        }

        public double getNewDamage() {
            return newDamage;
        }

        public void setNewDamage(double newDamage) {
            this.newDamage = newDamage;
        }

        public double getOriginDamage() {
            return originDamage;
        }
    }

    public static class BladeStandAttackBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
        private final BladeStandEntity bladeStand;
        private final DamageSource damageSource;

        public BladeStandAttackBaseEvent(ItemStack blade, ISlashBladeState state, BladeStandEntity bladeStand, DamageSource damageSource) {
            super(blade, state);
            this.bladeStand = bladeStand;
            this.damageSource = damageSource;
        }

        public BladeStandEntity getBladeStand() {
            return bladeStand;
        }

        public DamageSource getDamageSource() {
            return damageSource;
        }

    }

    public static class HitBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
        private final LivingEntity target;
        private final LivingEntity user;

        public HitBaseEvent(ItemStack blade, ISlashBladeState state, LivingEntity target, LivingEntity user) {
            super(blade, state);
            this.target = target;
            this.user = user;
        }

        public LivingEntity getUser() {
            return user;
        }

        public LivingEntity getTarget() {
            return target;
        }

    }

    public static class UpdateBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
        private final Level level;
        private final Entity entity;
        private final int itemSlot;
        private final boolean isSelected;

        public UpdateBaseEvent(ItemStack blade, ISlashBladeState state,
                               Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            super(blade, state);
            this.level = worldIn;
            this.entity = entityIn;
            this.itemSlot = itemSlot;
            this.isSelected = isSelected;
        }

        public Level getLevel() {
            return level;
        }

        public Entity getEntity() {
            return entity;
        }

        public int getItemSlot() {
            return itemSlot;
        }

        public boolean isSelected() {
            return isSelected;
        }

    }

    public static class DoSlashBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
        private final LivingEntity user;
        private float roll;
        private boolean critical;
        private double damage;
        private KnockBacks knockback;

        public DoSlashBaseEvent(ItemStack blade, ISlashBladeState state, LivingEntity user,
                                float roll, boolean critical, double damage, KnockBacks knockback) {
            super(blade, state);
            this.user = user;
            this.roll = roll;
            this.critical = critical;
            this.knockback = knockback;
            this.damage = damage;
        }

        public LivingEntity getUser() {
            return user;
        }

        public float getRoll() {
            return roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }

        public boolean isCritical() {
            return critical;
        }

        public void setCritical(boolean critical) {
            this.critical = critical;
        }

        public double getDamage() {
            return damage;
        }

        public void setDamage(double damage) {
            this.damage = damage;
        }

        public KnockBacks getKnockback() {
            return knockback;
        }

        public void setKnockback(KnockBacks knockback) {
            this.knockback = knockback;
        }

    }


    public static class ChargeActionBaseEvent extends BaseEvent implements ICancellableEvent {
        private final LivingEntity entityLiving;
        private final int elapsed;
        private final ISlashBladeState state;
        private ResourceLocation comboState;
        private final SlashArts.ArtsType type;

        public ChargeActionBaseEvent(LivingEntity entityLiving, int elapsed, ISlashBladeState state, ResourceLocation comboState, SlashArts.ArtsType type) {
            this.entityLiving = entityLiving;
            this.elapsed = elapsed;
            this.state = state;
            this.comboState = comboState;
            this.type = type;
        }

        public LivingEntity getEntityLiving() {
            return entityLiving;
        }

        public int getElapsed() {
            return elapsed;
        }

        public ISlashBladeState getSlashBladeState() {
            return state;
        }

        public ResourceLocation getComboState() {
            return comboState;
        }

        public void setComboState(ResourceLocation comboState) {
            this.comboState = comboState;
        }

        public SlashArts.ArtsType getType() {
            return type;
        }
    }
}
