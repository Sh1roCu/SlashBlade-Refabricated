package mods.flammpfeil.slashblade.capability.slashblade;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.NBTHelper;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ISlashBladeState {
    // action state
    String LAST_ACTION_TIME = "lastActionTime";
    String TARGET_ENTITY = "TargetEntity";
    String ON_CLICK = "_onClick";
    String FALL_DECREASE_RATE = "fallDecreaseRate";
    String ATTACK_AMPLIFIER = "AttackAmplifier";
    String CURRENT_COMBO = "currentCombo";
    String DAMAGE = "Damage";
    String MAX_DAMAGE = "maxDamage";
    String PROUD_SOUL = "proudSoul";
    String IS_BROKEN = "isBroken";

    // passive state
    String IS_SEALED = "isSealed";
    String BASE_ATTACK_MODIFIER = "baseAttackModifier";
    String KILL_COUNT = "killCount";
    String REPAIR_COUNTER = "RepairCounter";

    // performance setting
    String SPECIAL_ATTACK_TYPE = "SpecialAttackType";
    String IS_DEFAULT_BEWITCHED = "isDefaultBewitched";
    String TRANSLATION_KEY = "translationKey";

    // render info
    String STANDBY_RENDER_TYPE = "StandbyRenderType";
    String SUMMONED_SWORD_COLOR = "SummonedSwordColor";
    String SUMMONED_SWORD_COLOR_INVERSE = "SummonedSwordColorInverse";
    String ADJUST_XYZ = "adjustXYZ";
    String TEXTURE_NAME = "TextureName";
    String MODEL_NAME = "ModelName";

    String COMBO_ROOT = "ComboRoot";
    String SPECIAL_EFFECTS = "SpecialEffects";

    default CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        // action state
        tag.putLong("lastActionTime", this.getLastActionTime());
        tag.putInt("TargetEntity", this.getTargetEntityId());
        tag.putBoolean("_onClick", this.onClick());
        tag.putFloat("fallDecreaseRate", this.getFallDecreaseRate());
        tag.putFloat("AttackAmplifier", this.getAttackAmplifier());
        tag.putString("currentCombo", this.getComboSeq().toString());
        tag.putInt("Damage", this.getDamage());
        tag.putInt("maxDamage", this.getMaxDamage());
        tag.putInt("proudSoul", this.getProudSoulCount());
        tag.putBoolean("isBroken", this.isBroken());

        // passive state
        tag.putBoolean("isSealed", this.isSealed());

        tag.putFloat("baseAttackModifier", this.getBaseAttackModifier());

        tag.putInt("killCount", this.getKillCount());
        tag.putInt("RepairCounter", this.getRefine());

        // performance setting

        tag.putString("SpecialAttackType",
                Optional.ofNullable(this.getSlashArtsKey()).orElse(SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT)).toString());
        tag.putBoolean("isDefaultBewitched", this.isDefaultBewitched());
        tag.putString("translationKey", this.getTranslationKey());

        // render info
        tag.putByte("StandbyRenderType", (byte) this.getCarryType().ordinal());
        tag.putInt("SummonedSwordColor", this.getColorCode());
        tag.putBoolean("SummonedSwordColorInverse", this.isEffectColorInverse());
        tag.put("adjustXYZ", NBTHelper.newDoubleNBTList(this.getAdjust()));

        this.getTexture().ifPresent(loc -> tag.putString("TextureName", loc.toString()));
        this.getModel().ifPresent(loc -> tag.putString("ModelName", loc.toString()));

        tag.putString("ComboRoot",
                Optional.ofNullable(this.getComboRoot()).orElse(ComboStateRegistry.getId(ComboStateRegistry.STANDBY)).toString());

        if (this.getSpecialEffects() != null && !this.getSpecialEffects().isEmpty()) {
            ListTag seList = new ListTag();
            this.getSpecialEffects().forEach(se -> seList.add(StringTag.valueOf(se.toString())));
            tag.put("SpecialEffects", seList);
        }

        return tag;
    }

    default void deserializeNBT(CompoundTag tag) {
        if (tag == null)
            return;
        this.setNonEmpty();
        // action state
        this.setLastActionTime(tag.getLong("lastActionTime"));
        this.setTargetEntityId(tag.getInt("TargetEntity"));
        this.setOnClick(tag.getBoolean("_onClick"));
        this.setFallDecreaseRate(tag.getFloat("fallDecreaseRate"));
        this.setAttackAmplifier(tag.getFloat("AttackAmplifier"));
        this.setComboSeq(ResourceLocation.tryParse(tag.getString("currentCombo")));
        this.setDamage(tag.getInt("Damage"));
        this.setMaxDamage(tag.getInt("maxDamage"));
        this.setProudSoulCount(tag.getInt("proudSoul"));
        this.setBroken(tag.getBoolean("isBroken"));

        // passive state
        this.setSealed(tag.getBoolean("isSealed"));

        this.setBaseAttackModifier(tag.getFloat("baseAttackModifier"));

        this.setKillCount(tag.getInt("killCount"));
        this.setRefine(tag.getInt("RepairCounter"));

        // performance setting

        this.setSlashArtsKey(ResourceLocation.tryParse(tag.getString("SpecialAttackType")));
        this.setDefaultBewitched(tag.getBoolean("isDefaultBewitched"));

        this.setTranslationKey(tag.getString("translationKey"));

        // render info
        this.setCarryType(
                EnumSetConverter.fromOrdinal(CarryType.values(), tag.getByte("StandbyRenderType"), CarryType.PSO2));
        this.setColorCode(tag.getInt("SummonedSwordColor"));
        this.setEffectColorInverse(tag.getBoolean("SummonedSwordColorInverse"));
        this.setAdjust(NBTHelper.getVector3d(tag, "adjustXYZ"));

        if (tag.contains("TextureName"))
            this.setTexture(new ResourceLocation(tag.getString("TextureName")));
        else
            this.setTexture(null);

        if (tag.contains("ModelName"))
            this.setModel(new ResourceLocation(tag.getString("ModelName")));
        else
            this.setModel(null);

        this.setComboRoot(ResourceLocation.tryParse(tag.getString("ComboRoot")));
        if (tag.contains("SpecialEffects")) {
            ListTag list = tag.getList("SpecialEffects", 8);
            this.setSpecialEffects(list);
        }

    }

    long getLastActionTime();

    void setLastActionTime(long lastActionTime);

    default long getElapsedTime(LivingEntity user) {
        long ticks = (Math.max(0, user.level().getGameTime() - this.getLastActionTime()));

        if (user.level().isClientSide())
            ticks = Math.max(0, ticks + 1);

        return ticks;
    }

    boolean onClick();

    void setOnClick(boolean onClick);

    float getFallDecreaseRate();

    void setFallDecreaseRate(float fallDecreaseRate);

    float getAttackAmplifier();

    void setAttackAmplifier(float attackAmplifier);

    @Nonnull
    ResourceLocation getComboSeq();

    void setComboSeq(ResourceLocation comboSeq);

    boolean isBroken();

    void setBroken(boolean broken);

    boolean isSealed();

    void setSealed(boolean sealed);

    float getBaseAttackModifier();

    void setBaseAttackModifier(float baseAttackModifier);

    int getProudSoulCount();

    void setProudSoulCount(int psCount);

    int getKillCount();

    void setKillCount(int killCount);

    int getRefine();

    void setRefine(int refine);

    @Nonnull
    default SlashArts getSlashArts() {
        ResourceLocation key = getSlashArtsKey();
        SlashArts result = null;
        if (key != null)
            result = SlashArtsRegistry.SLASH_ARTS.containsKey(key) ? SlashArtsRegistry.SLASH_ARTS.get(key)
                    : SlashArtsRegistry.JUDGEMENT_CUT;

        if (key == SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.NONE))
            result = null;

        return result != null ? result : SlashArtsRegistry.JUDGEMENT_CUT;
    }

    void setSlashArtsKey(ResourceLocation slashArts);

    ResourceLocation getSlashArtsKey();

    boolean isDefaultBewitched();

    void setDefaultBewitched(boolean defaultBewitched);

    @NotNull
    String getTranslationKey();

    void setTranslationKey(String translationKey);

    @NotNull
    CarryType getCarryType();

    void setCarryType(CarryType carryType);

    @NotNull
    Color getEffectColor();

    void setEffectColor(Color effectColor);

    boolean isEffectColorInverse();

    void setEffectColorInverse(boolean effectColorInverse);

    default void setColorCode(int colorCode) {
        setEffectColor(new Color(colorCode));
    }

    default int getColorCode() {
        return getEffectColor().getRGB();
    }

    @NotNull
    Vec3 getAdjust();

    void setAdjust(Vec3 adjust);

    @NotNull
    Optional<ResourceLocation> getTexture();

    void setTexture(ResourceLocation texture);

    @NotNull
    Optional<ResourceLocation> getModel();

    void setModel(ResourceLocation model);

    int getTargetEntityId();

    void setTargetEntityId(int id);

    @Nullable
    default Entity getTargetEntity(Level world) {
        int id = getTargetEntityId();
        if (id < 0)
            return null;
        else
            return world.getEntity(id);
    }

    default void setTargetEntityId(Entity target) {
        if (target != null)
            this.setTargetEntityId(target.getId());
        else
            this.setTargetEntityId(-1);
    }

    default int getFullChargeTicks(LivingEntity user) {
        return SlashArts.ChargeTicks;
    }

    default boolean isCharged(LivingEntity user) {
        if (!(SwordType.from(user.getMainHandItem()).contains(SwordType.ENCHANTED)))
            return false;
        if (this.isBroken() || this.isSealed())
            return false;
        int elapsed = user.getTicksUsingItem();
        return getFullChargeTicks(user) < elapsed;
    }

    default ResourceLocation progressCombo(LivingEntity user, boolean isVirtual) {
        ResourceLocation currentloc = resolvCurrentComboState(user);
        ComboState current = ComboStateRegistry.COMBO_STATE.get(currentloc);

        if (current == null)
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        ResourceLocation next = current.getNext(user);
        if (!next.equals(ComboStateRegistry.getId(ComboStateRegistry.NONE)) && next.equals(currentloc))
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        ResourceLocation rootNext = ComboStateRegistry.COMBO_STATE.get(getComboRoot()).getNext(user);
        ComboState nextCS = ComboStateRegistry.COMBO_STATE.get(next);
        ComboState rootNextCS = ComboStateRegistry.COMBO_STATE.get(rootNext);
        ResourceLocation resolved = nextCS.getPriority() <= rootNextCS.getPriority() ? next : rootNext;

        if (!isVirtual) {
            this.updateComboSeq(user, resolved);
        }

        return resolved;
    }

    default ResourceLocation progressCombo(LivingEntity user) {
        return progressCombo(user, false);
    }

    default ResourceLocation doChargeAction(LivingEntity user, int elapsed) {
        if (elapsed <= 2)
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        if (this.isBroken() || this.isSealed())
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        Map.Entry<Integer, ResourceLocation> currentloc = resolvCurrentComboStateTicks(user);

        ComboState current = ComboStateRegistry.COMBO_STATE.get(currentloc.getValue());
        if (current == null)
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        // Uninterrupted
        if (currentloc.getValue() != ComboStateRegistry.getId(ComboStateRegistry.NONE) && current.getNext(user) == currentloc.getValue())
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);

        int fullChargeTicks = getFullChargeTicks(user);
        int justReceptionSpan = SlashArts.getJustReceptionSpan(user);
        int justChargePeriod = fullChargeTicks + justReceptionSpan;

        @SuppressWarnings("UnstableApiUsage")
        RangeMap<Integer, SlashArts.ArtsType> charge_accept = ImmutableRangeMap.<Integer, SlashArts.ArtsType>builder()
                .put(Range.lessThan(fullChargeTicks), SlashArts.ArtsType.Fail)
                .put(Range.closedOpen(fullChargeTicks, justChargePeriod), SlashArts.ArtsType.Jackpot)
                .put(Range.atLeast(justChargePeriod), SlashArts.ArtsType.Success).build();

        SlashArts.ArtsType type = charge_accept.get(elapsed);

        if (type != SlashArts.ArtsType.Jackpot) {
            // quick charge
            SlashArts.ArtsType result = current.releaseAction(user, currentloc.getKey());

            if (result != SlashArts.ArtsType.Fail)
                type = result;
        }

        ResourceLocation csloc = this.getSlashArts().doArts(type, user);

        SlashBladeBaseEvent.ChargeActionBaseEvent event = new SlashBladeBaseEvent.ChargeActionBaseEvent(user, elapsed, this, csloc,
                type);
        SlashBladeBaseEvent.CHARGE_ACTION.invoker().onChargeAction(event);
        if (event.isCanceled()) {
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);
        }

        csloc = event.getComboState();
        ComboState cs = ComboStateRegistry.COMBO_STATE.get(csloc);

        if (csloc != ComboStateRegistry.getId(ComboStateRegistry.NONE) && !currentloc.getValue().equals(csloc)) {

            if (current.getPriority() > cs.getPriority()) {
                if (type == SlashArts.ArtsType.Jackpot)
                    AdvancementHelper.grantedIf(Enchantments.SOUL_SPEED, user);
                this.updateComboSeq(user, csloc);
            }
        }
        return csloc;
    }

    default void updateComboSeq(LivingEntity entity, ResourceLocation loc) {
        BladeMotionEvent.CALLBACK.invoker().onBladeMotion(new BladeMotionEvent(entity, loc));
        this.setComboSeq(loc);
        this.setLastActionTime(entity.level().getGameTime());
        ComboState cs = ComboStateRegistry.COMBO_STATE.get(loc);
        cs.clickAction(entity);
    }

    default ResourceLocation resolvCurrentComboState(LivingEntity user) {
        if (!(user.getMainHandItem().getItem() instanceof ItemSlashBlade))
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);
        return resolvCurrentComboStateTicks(user).getValue();
    }

    default Map.Entry<Integer, ResourceLocation> resolvCurrentComboStateTicks(LivingEntity user) {
        ResourceLocation current = ComboStateRegistry.COMBO_STATE.containsKey(getComboSeq()) ? getComboSeq()
                : ComboStateRegistry.getId(ComboStateRegistry.NONE);
        ComboState currentCS = ComboStateRegistry.COMBO_STATE.get(current) != null
                ? ComboStateRegistry.COMBO_STATE.get(current)
                : ComboStateRegistry.NONE;
        int time = (int) TimeValueHelper.getMSecFromTicks(getElapsedTime(user));

        while (!current.equals(ComboStateRegistry.getId(ComboStateRegistry.NONE)) && currentCS.getTimeoutMS() < time) {
            time -= currentCS.getTimeoutMS();

            current = currentCS.getNextOfTimeout(user);
            this.updateComboSeq(user, current);
        }

        int ticks = (int) TimeValueHelper.getTicksFromMSec(time);
        return new AbstractMap.SimpleImmutableEntry<>(ticks, current);
    }

    ResourceLocation getComboRoot();

    void setComboRoot(ResourceLocation resourceLocation);

    int getDamage();

    void setDamage(int damage);

    int getMaxDamage();

    void setMaxDamage(int damage);

    List<ResourceLocation> getSpecialEffects();

    void setSpecialEffects(ListTag list);

    boolean addSpecialEffect(ResourceLocation se);

    boolean removeSpecialEffect(ResourceLocation se);

    boolean hasSpecialEffect(ResourceLocation se);

    boolean isEmpty();

    void setNonEmpty();
}