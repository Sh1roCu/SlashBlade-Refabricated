package mods.flammpfeil.slashblade.capability.slashblade;

import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reference implementation of {@link ISlashBladeState}. Use/extend this or
 * implement your own.
 * <p>
 */
public class SlashBladeState extends ItemComponent implements ISlashBladeState {
    private static final String IS_EMPTY = "isEmpty";

    public SlashBladeState(ItemStack blade) {
        super(blade);
    }

    @Override
    public long getLastActionTime() {
        return getLong(LAST_ACTION_TIME);
    }

    @Override
    public void setLastActionTime(long lastActionTime) {
        putLong(LAST_ACTION_TIME, lastActionTime);
    }

    @Override
    public boolean onClick() {
        return getBoolean(ON_CLICK);
    }

    @Override
    public void setOnClick(boolean onClick) {
        putBoolean(ON_CLICK, onClick);
    }

    @Override
    public float getFallDecreaseRate() {
        return getFloat(FALL_DECREASE_RATE);
    }

    @Override
    public void setFallDecreaseRate(float fallDecreaseRate) {
        putFloat(FALL_DECREASE_RATE, fallDecreaseRate);

    }

    @Override
    public float getAttackAmplifier() {
        return getFloat(ATTACK_AMPLIFIER);
    }

    @Override
    public void setAttackAmplifier(float attackAmplifier) {
        putFloat(ATTACK_AMPLIFIER, attackAmplifier);
    }

    @Override
    @Nonnull
    public ResourceLocation getComboSeq() {
        if (getString(CURRENT_COMBO).isEmpty())
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);
        ResourceLocation location = ResourceLocation.tryParse(getString(CURRENT_COMBO));
        return location != null && ComboStateRegistry.COMBO_STATE.containsKey(location) ? location : ComboStateRegistry.getId(ComboStateRegistry.NONE);
    }

    @Override
    public void setComboSeq(ResourceLocation comboSeq) {
        if (ComboStateRegistry.COMBO_STATE.containsKey(comboSeq))
            putString(CURRENT_COMBO, comboSeq.toString());
        else putString(CURRENT_COMBO, ComboStateRegistry.getId(ComboStateRegistry.NONE).toString());
    }

    @Override
    public boolean isBroken() {
        return getBoolean(IS_BROKEN);
    }

    @Override
    public void setBroken(boolean broken) {
        putBoolean(IS_BROKEN, broken);
    }

    @Override
    public boolean isSealed() {
        return getBoolean(IS_SEALED);
    }

    @Override
    public void setSealed(boolean sealed) {
        putBoolean(IS_SEALED, sealed);
    }

    @Override
    public float getBaseAttackModifier() {
        return getFloat(BASE_ATTACK_MODIFIER);
    }

    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
        putFloat(BASE_ATTACK_MODIFIER, baseAttackModifier);
    }

    @Override
    public int getKillCount() {
        return getInt(KILL_COUNT);
    }

    @Override
    public void setKillCount(int killCount) {
        putInt(KILL_COUNT, killCount);
    }

    @Override
    public int getRefine() {
        return getInt(REPAIR_COUNTER);
    }

    @Override
    public void setRefine(int refine) {
        putInt(REPAIR_COUNTER, refine);
    }

    @Override
    public ResourceLocation getSlashArtsKey() {
        if (getString(SPECIAL_ATTACK_TYPE).isEmpty())
            return SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
        ResourceLocation location = ResourceLocation.tryParse(getString(SPECIAL_ATTACK_TYPE));
        return location != null ? location : SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
    }

    @Override
    public void setSlashArtsKey(ResourceLocation key) {
        if (SlashArtsRegistry.SLASH_ARTS.containsKey(key))
            putString(SPECIAL_ATTACK_TYPE, key.toString());
        else
            putString(SPECIAL_ATTACK_TYPE, SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT).toString());
    }

    @Override
    public boolean isDefaultBewitched() {
        return getBoolean(IS_DEFAULT_BEWITCHED);
    }

    @Override
    public void setDefaultBewitched(boolean defaultBewitched) {
        putBoolean(IS_DEFAULT_BEWITCHED, defaultBewitched);
    }

    @Override
    public @NotNull String getTranslationKey() {
        return getString(TRANSLATION_KEY);
    }

    @Override
    public void setTranslationKey(String translationKey) {
        putString(TRANSLATION_KEY, Optional.ofNullable(translationKey).orElse(""));
    }

    @Override
    @Nonnull
    public CarryType getCarryType() {
        return EnumSetConverter.fromOrdinal(CarryType.values(), getInt(STANDBY_RENDER_TYPE), CarryType.PSO2);
    }

    @Override
    public void setCarryType(CarryType carryType) {
        putInt(STANDBY_RENDER_TYPE, carryType.ordinal());
    }

    @Override
    public @NotNull Color getEffectColor() {
        if (hasTag(SUMMONED_SWORD_COLOR))
            return new Color(getInt(SUMMONED_SWORD_COLOR));
        return new Color(0x3333FF);
    }

    @Override
    public void setEffectColor(Color effectColor) {
        putInt(SUMMONED_SWORD_COLOR, effectColor.getRGB());
    }

    @Override
    public boolean isEffectColorInverse() {
        return getBoolean(SUMMONED_SWORD_COLOR_INVERSE);
    }

    @Override
    public void setEffectColorInverse(boolean effectColorInverse) {
        putBoolean(SUMMONED_SWORD_COLOR_INVERSE, effectColorInverse);
    }

    @Override
    public @NotNull Vec3 getAdjust() {
        if (hasTag(ADJUST_XYZ))
            return NBTHelper.getVector3d(getOrCreateRootTag(), ADJUST_XYZ);
        return Vec3.ZERO;
    }

    @Override
    public void setAdjust(Vec3 adjust) {
        putList(ADJUST_XYZ, NBTHelper.newDoubleNBTList(adjust));
    }

    @Override
    public @NotNull Optional<ResourceLocation> getTexture() {
        if (getString(TEXTURE_NAME).isEmpty())
            return Optional.empty();
        return Optional.ofNullable(ResourceLocation.tryParse(getString(TEXTURE_NAME)));
    }

    @Override
    public void setTexture(ResourceLocation texture) {
        if (texture != null)
            putString(TEXTURE_NAME, texture.toString());
    }

    @Override
    public @NotNull Optional<ResourceLocation> getModel() {
        if (getString(MODEL_NAME).isEmpty())
            return Optional.empty();
        return Optional.ofNullable(ResourceLocation.tryParse(getString(MODEL_NAME)));
    }

    @Override
    public void setModel(ResourceLocation model) {
        if (model != null)
            putString(MODEL_NAME, model.toString());
    }

    @Override
    public int getTargetEntityId() {
        return getInt(TARGET_ENTITY);
    }

    @Override
    public void setTargetEntityId(int id) {
        putInt(TARGET_ENTITY, id);
    }

    @Override
    public ResourceLocation getComboRoot() {
        if (getString(COMBO_ROOT).isEmpty())
            return ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
        ResourceLocation location = ResourceLocation.tryParse(getString(COMBO_ROOT));
        return location != null && ComboStateRegistry.COMBO_STATE.containsKey(location) ? location : ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
    }

    @Override
    public void setComboRoot(ResourceLocation rootLoc) {
        if (ComboStateRegistry.COMBO_STATE.containsKey(rootLoc))
            putString(COMBO_ROOT, rootLoc.toString());
        else putString(COMBO_ROOT, ComboStateRegistry.getId(ComboStateRegistry.STANDBY).toString());
    }

    @Override
    public int getMaxDamage() {
        return getInt(MAX_DAMAGE);
    }

    @Override
    public void setMaxDamage(int damage) {
        putInt(MAX_DAMAGE, damage);
    }

    @Override
    public int getDamage() {
        return getInt(DAMAGE);
    }

    @Override
    public void setDamage(int damage) {
        putInt(DAMAGE, Math.max(0, damage));
    }

    @Override
    public int getProudSoulCount() {
        return getInt(PROUD_SOUL);
    }

    @Override
    public void setProudSoulCount(int psCount) {
        putInt(PROUD_SOUL, Math.max(0, psCount));
    }

    @Override
    public List<ResourceLocation> getSpecialEffects() {
        List<ResourceLocation> result = new ArrayList<>();
        getList(SPECIAL_EFFECTS, CcaNbtType.STRING).forEach(tag -> {
            ResourceLocation se = ResourceLocation.tryParse(tag.getAsString());
            if (se != null && SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se))
                result.add(se);
        });
        return result;
    }

    @Override
    public void setSpecialEffects(ListTag list) {
        putList(SPECIAL_EFFECTS, list);
    }

    @Override
    public boolean addSpecialEffect(ResourceLocation se) {
        List<ResourceLocation> temp = getSpecialEffects();
        boolean added = temp.add(se);
        ListTag listTag = new ListTag();
        temp.forEach(location -> listTag.add(StringTag.valueOf(location.toString())));
        setSpecialEffects(listTag);
        return added;
    }

    @Override
    public boolean removeSpecialEffect(ResourceLocation se) {
        List<ResourceLocation> temp = getSpecialEffects();
        boolean removed = temp.remove(se);
        if (removed) {
            ListTag listTag = new ListTag();
            temp.forEach(location -> listTag.add(StringTag.valueOf(location.toString())));
            setSpecialEffects(listTag);
        }
        return removed;
    }

    @Override
    public boolean hasSpecialEffect(ResourceLocation se) {
        if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
            return getList(SPECIAL_EFFECTS, CcaNbtType.STRING).stream().anyMatch(tag -> tag.getAsString().equals(se.toString()));
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return getBoolean(IS_EMPTY);
    }

    @Override
    public void setNonEmpty() {
        putBoolean(IS_EMPTY, false);
    }
}
