package mods.flammpfeil.slashblade.capability.slashblade;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

    private CompoundTag getBladeState() {
        return stack.getOrCreateTagElement("bladeState");
    }

    @Override
    public long getLastActionTime() {
        return getBladeState().getLong(LAST_ACTION_TIME);
    }

    @Override
    public void setLastActionTime(long lastActionTime) {
        getBladeState().putLong(LAST_ACTION_TIME, lastActionTime);
    }

    @Override
    public boolean onClick() {
        return getBladeState().getBoolean(ON_CLICK);
    }

    @Override
    public void setOnClick(boolean onClick) {
        getBladeState().putBoolean(ON_CLICK, onClick);
    }

    @Override
    public float getFallDecreaseRate() {
        return getBladeState().getFloat(FALL_DECREASE_RATE);
    }

    @Override
    public void setFallDecreaseRate(float fallDecreaseRate) {
        getBladeState().putFloat(FALL_DECREASE_RATE, fallDecreaseRate);
    }

    @Override
    public float getAttackAmplifier() {
        return getBladeState().getFloat(ATTACK_AMPLIFIER);
    }

    @Override
    public void setAttackAmplifier(float attackAmplifier) {
        getBladeState().putFloat(ATTACK_AMPLIFIER, attackAmplifier);
    }

    @Override
    @Nonnull
    public ResourceLocation getComboSeq() {
        if (getBladeState().getString(CURRENT_COMBO).isEmpty())
            return ComboStateRegistry.getId(ComboStateRegistry.NONE);
        ResourceLocation location = ResourceLocation.tryParse(getBladeState().getString(CURRENT_COMBO));
        return location != null && ComboStateRegistry.COMBO_STATE.containsKey(location) ? location : ComboStateRegistry.getId(ComboStateRegistry.NONE);
    }

    @Override
    public void setComboSeq(ResourceLocation comboSeq) {
        if (ComboStateRegistry.COMBO_STATE.containsKey(comboSeq)) {
            getBladeState().putString(CURRENT_COMBO, comboSeq.toString());
        } else {
            ResourceLocation id = ComboStateRegistry.getId(ComboStateRegistry.NONE);
            getBladeState().putString(CURRENT_COMBO, id.toString());
        }
    }

    @Override
    public boolean isBroken() {
        return getBladeState().getBoolean(IS_BROKEN);
    }

    @Override
    public void setBroken(boolean broken) {
        getBladeState().putBoolean(IS_BROKEN, broken);
    }

    @Override
    public boolean isSealed() {
        return getBladeState().getBoolean(IS_SEALED);
    }

    @Override
    public void setSealed(boolean sealed) {
        getBladeState().putBoolean(IS_SEALED, sealed);
    }

    @Override
    public float getBaseAttackModifier() {
        CompoundTag bladeState = getBladeState();
        if (bladeState.contains(BASE_ATTACK_MODIFIER))
            return bladeState.getFloat(BASE_ATTACK_MODIFIER);
        // 默认值
        return 4F;
    }

    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
        getBladeState().putFloat(BASE_ATTACK_MODIFIER, baseAttackModifier);
    }

    @Override
    public int getKillCount() {
        return getBladeState().getInt(KILL_COUNT);
    }

    @Override
    public void setKillCount(int killCount) {
        getBladeState().putInt(KILL_COUNT, killCount);
    }

    @Override
    public int getRefine() {
        return getBladeState().getInt(REPAIR_COUNTER);
    }

    @Override
    public void setRefine(int refine) {
        getBladeState().putInt(REPAIR_COUNTER, refine);
    }

    @Override
    public ResourceLocation getSlashArtsKey() {
        if (getBladeState().getString(SPECIAL_ATTACK_TYPE).isEmpty())
            return SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
        ResourceLocation location = ResourceLocation.tryParse(getBladeState().getString(SPECIAL_ATTACK_TYPE));
        return location != null ? location : SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
    }

    @Override
    public void setSlashArtsKey(ResourceLocation key) {
        if (SlashArtsRegistry.SLASH_ARTS.containsKey(key)) {
            getBladeState().putString(SPECIAL_ATTACK_TYPE, key.toString());
        } else {
            ResourceLocation id = SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
            getBladeState().putString(SPECIAL_ATTACK_TYPE, id.toString());
        }
    }

    @Override
    public boolean isDefaultBewitched() {
        return getBladeState().getBoolean(IS_DEFAULT_BEWITCHED);
    }

    @Override
    public void setDefaultBewitched(boolean defaultBewitched) {
        getBladeState().putBoolean(IS_DEFAULT_BEWITCHED, defaultBewitched);
    }

    @Override
    public @NotNull String getTranslationKey() {
        return getBladeState().getString(TRANSLATION_KEY);
    }

    @Override
    public void setTranslationKey(String translationKey) {
        String key = Optional.ofNullable(translationKey).orElse("");
        getBladeState().putString(TRANSLATION_KEY, key);
    }

    @Override
    @Nonnull
    public CarryType getCarryType() {
        return EnumSetConverter.fromOrdinal(CarryType.values(), getBladeState().getInt(STANDBY_RENDER_TYPE), CarryType.PSO2);
    }

    @Override
    public void setCarryType(CarryType carryType) {
        getBladeState().putInt(STANDBY_RENDER_TYPE, carryType.ordinal());
    }

    @Override
    public @NotNull Color getEffectColor() {
        if (getBladeState().contains(SUMMONED_SWORD_COLOR))
            return new Color(getBladeState().getInt(SUMMONED_SWORD_COLOR));
        return new Color(0x3333FF);
    }

    @Override
    public void setEffectColor(Color effectColor) {
        getBladeState().putInt(SUMMONED_SWORD_COLOR, effectColor.getRGB());
    }

    @Override
    public boolean isEffectColorInverse() {
        return getBladeState().getBoolean(SUMMONED_SWORD_COLOR_INVERSE);
    }

    @Override
    public void setEffectColorInverse(boolean effectColorInverse) {
        getBladeState().putBoolean(SUMMONED_SWORD_COLOR_INVERSE, effectColorInverse);
    }

    @Override
    public @NotNull Vec3 getAdjust() {
        if (getBladeState().contains(ADJUST_XYZ))
            return NBTHelper.getVector3d(getOrCreateRootTag(), ADJUST_XYZ);
        return Vec3.ZERO;
    }

    @Override
    public void setAdjust(Vec3 adjust) {
        getBladeState().put(ADJUST_XYZ, NBTHelper.newDoubleNBTList(adjust));
    }

    @Override
    public @NotNull Optional<ResourceLocation> getTexture() {
        ResourceLocation location = ResourceLocation.tryParse(getBladeState().getString(TEXTURE_NAME));
        if (location != null && location.getPath().isEmpty())
            return Optional.empty();
        return Optional.ofNullable(location);
    }

    @Override
    public void setTexture(ResourceLocation texture) {
        if (texture != null) {
            getBladeState().putString(TEXTURE_NAME, texture.toString());
        }
    }

    @Override
    public @NotNull Optional<ResourceLocation> getModel() {
        ResourceLocation location = ResourceLocation.tryParse(getBladeState().getString(MODEL_NAME));
        if (location != null && location.getPath().isEmpty())
            return Optional.empty();
        return Optional.ofNullable(location);
    }

    @Override
    public void setModel(ResourceLocation model) {
        if (model != null) {
            getBladeState().putString(MODEL_NAME, model.toString());
        }
    }

    @Override
    public int getTargetEntityId() {
        return getBladeState().getInt(TARGET_ENTITY);
    }

    @Override
    public void setTargetEntityId(int id) {
        getBladeState().putInt(TARGET_ENTITY, id);
    }

    @Override
    public ResourceLocation getComboRoot() {
        if (getBladeState().getString(COMBO_ROOT).isEmpty())
            return ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
        ResourceLocation location = ResourceLocation.tryParse(getBladeState().getString(COMBO_ROOT));
        return location != null && ComboStateRegistry.COMBO_STATE.containsKey(location) ? location : ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
    }

    @Override
    public void setComboRoot(ResourceLocation rootLoc) {
        if (ComboStateRegistry.COMBO_STATE.containsKey(rootLoc)) {
            getBladeState().putString(COMBO_ROOT, rootLoc.toString());
        } else {
            ResourceLocation id = ComboStateRegistry.getId(ComboStateRegistry.STANDBY);
            getBladeState().putString(COMBO_ROOT, id.toString());
        }
    }

    @Override
    public int getMaxDamage() {
        CompoundTag bladeState = getBladeState();
        if (bladeState.contains(MAX_DAMAGE))
            return bladeState.getInt(MAX_DAMAGE);
        // 默认值
        return 40;
    }

    @Override
    public void setMaxDamage(int damage) {
        getBladeState().putInt(MAX_DAMAGE, damage);
    }

    @Override
    public int getDamage() {
        return getBladeState().getInt(DAMAGE);
    }

    @Override
    public void setDamage(int damage) {
        int count = Math.max(0, damage);
        getBladeState().putInt(DAMAGE, count);
    }

    @Override
    public int getProudSoulCount() {
        return getBladeState().getInt(PROUD_SOUL);
    }

    @Override
    public void setProudSoulCount(int psCount) {
        int count = Math.max(0, psCount);
        getBladeState().putInt(PROUD_SOUL, count);
    }

    @Override
    public List<ResourceLocation> getSpecialEffects() {
        List<ResourceLocation> result = new ArrayList<>();
        getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).forEach(tag -> {
            ResourceLocation se = ResourceLocation.tryParse(tag.getAsString());
            if (se != null && SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se))
                result.add(se);
        });
        return result;
    }

    @Override
    public void setSpecialEffects(ListTag list) {
        getBladeState().put(SPECIAL_EFFECTS, list);
    }

    @Override
    public boolean addSpecialEffect(ResourceLocation se) {
        if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
            if (!getBladeState().contains(SPECIAL_EFFECTS))
                getBladeState().put(SPECIAL_EFFECTS, new ListTag());
            return getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).add(StringTag.valueOf(se.toString()));
        }
        return false;
    }

    @Override
    public boolean removeSpecialEffect(ResourceLocation se) {
        return getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).remove(StringTag.valueOf(se.toString()));
    }

    @Override
    public boolean hasSpecialEffect(ResourceLocation se) {
        if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
            return getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).contains(StringTag.valueOf(se.toString()));
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        CompoundTag bladeState = getBladeState();
        if (bladeState.contains(IS_EMPTY))
            return bladeState.getBoolean(IS_EMPTY);
        // 默认值
        return true;
    }

    @Override
    public void setNonEmpty() {
        getBladeState().putBoolean(IS_EMPTY, false);
    }
}
