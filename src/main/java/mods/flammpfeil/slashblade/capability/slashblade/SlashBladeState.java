package mods.flammpfeil.slashblade.capability.slashblade;

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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;

public class SlashBladeState implements ISlashBladeState {
    protected boolean isEmpty = true;
    protected ItemStack blade;

    public SlashBladeState(ItemStack blade) {
        this.blade = blade;
        setNonEmpty();
    }

    @Override
    public CompoundTag getBladeState() {
        return blade.getOrDefault(CapabilitySlashBlade.BLADESTATE_COMPONENT, CustomData.EMPTY).copyTag();
    }

    @Override
    public void setBladeState(CompoundTag tag) {
        CustomData.set(CapabilitySlashBlade.BLADESTATE_COMPONENT, blade, tag);
    }

    private void updateBladeState(Consumer<CompoundTag> consumer) {
        CustomData.update(CapabilitySlashBlade.BLADESTATE_COMPONENT, blade, consumer);
    }

    @Override
    public long getLastActionTime() {
        return getBladeState().getLong(LAST_ACTION_TIME);
    }

    @Override
    public void setLastActionTime(long lastActionTime) {
        updateBladeState(tag -> tag.putLong(LAST_ACTION_TIME, lastActionTime));
    }

    @Override
    public boolean onClick() {
        return getBladeState().getBoolean(ON_CLICK);
    }

    @Override
    public void setOnClick(boolean onClick) {
        updateBladeState(tag -> tag.putBoolean(ON_CLICK, onClick));
    }

    @Override
    public float getFallDecreaseRate() {
        return getBladeState().getFloat(FALL_DECREASE_RATE);
    }

    @Override
    public void setFallDecreaseRate(float fallDecreaseRate) {
        updateBladeState(tag -> tag.putFloat(FALL_DECREASE_RATE, fallDecreaseRate));
    }

    @Override
    public float getAttackAmplifier() {
        return getBladeState().getFloat(ATTACK_AMPLIFIER);
    }

    @Override
    public void setAttackAmplifier(float attackAmplifier) {
        updateBladeState(tag -> tag.putFloat(ATTACK_AMPLIFIER, attackAmplifier));
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
            updateBladeState(tag -> tag.putString(CURRENT_COMBO, comboSeq.toString()));
        } else {
            ResourceLocation id = ComboStateRegistry.getId(ComboStateRegistry.NONE);
            updateBladeState(tag -> tag.putString(CURRENT_COMBO, id.toString()));
        }
    }

    @Override
    public boolean isBroken() {
        return getBladeState().getBoolean(IS_BROKEN);
    }

    @Override
    public void setBroken(boolean broken) {
        updateBladeState(tag -> tag.putBoolean(IS_BROKEN, broken));
    }

    @Override
    public boolean isSealed() {
        return getBladeState().getBoolean(IS_SEALED);
    }

    @Override
    public void setSealed(boolean sealed) {
        updateBladeState(tag -> tag.putBoolean(IS_SEALED, sealed));
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
        updateBladeState(tag -> tag.putFloat(BASE_ATTACK_MODIFIER, baseAttackModifier));
    }

    @Override
    public int getKillCount() {
        return getBladeState().getInt(KILL_COUNT);
    }

    @Override
    public void setKillCount(int killCount) {
        updateBladeState(tag -> tag.putInt(KILL_COUNT, killCount));
    }

    @Override
    public int getRefine() {
        return getBladeState().getInt(REPAIR_COUNTER);
    }

    @Override
    public void setRefine(int refine) {
        updateBladeState(tag -> tag.putInt(REPAIR_COUNTER, refine));
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
            updateBladeState(tag -> tag.putString(SPECIAL_ATTACK_TYPE, key.toString()));
        } else {
            ResourceLocation id = SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.JUDGEMENT_CUT);
            updateBladeState(tag -> tag.putString(SPECIAL_ATTACK_TYPE, id.toString()));
        }
    }

    @Override
    public boolean isDefaultBewitched() {
        return getBladeState().getBoolean(IS_DEFAULT_BEWITCHED);
    }

    @Override
    public void setDefaultBewitched(boolean defaultBewitched) {
        updateBladeState(tag -> tag.putBoolean(IS_DEFAULT_BEWITCHED, defaultBewitched));
    }

    @Override
    public @NotNull String getTranslationKey() {
        return getBladeState().getString(TRANSLATION_KEY);
    }

    @Override
    public void setTranslationKey(String translationKey) {
        String key = Optional.ofNullable(translationKey).orElse("");
        updateBladeState(tag -> tag.putString(TRANSLATION_KEY, key));
    }

    @Override
    @Nonnull
    public CarryType getCarryType() {
        return EnumSetConverter.fromOrdinal(CarryType.values(), getBladeState().getInt(STANDBY_RENDER_TYPE), CarryType.PSO2);
    }

    @Override
    public void setCarryType(CarryType carryType) {
        updateBladeState(tag -> tag.putInt(STANDBY_RENDER_TYPE, carryType.ordinal()));
    }

    @Override
    public @NotNull Color getEffectColor() {
        if (getBladeState().contains(SUMMONED_SWORD_COLOR))
            return new Color(getBladeState().getInt(SUMMONED_SWORD_COLOR));
        return new Color(0x3333FF);
    }

    @Override
    public void setEffectColor(Color effectColor) {
        updateBladeState(tag -> tag.putInt(SUMMONED_SWORD_COLOR, effectColor.getRGB()));
    }

    @Override
    public boolean isEffectColorInverse() {
        return getBladeState().getBoolean(SUMMONED_SWORD_COLOR_INVERSE);
    }

    @Override
    public void setEffectColorInverse(boolean effectColorInverse) {
        updateBladeState(tag -> tag.putBoolean(SUMMONED_SWORD_COLOR_INVERSE, effectColorInverse));
    }

    @Override
    public @NotNull Vec3 getAdjust() {
        if (getBladeState().contains(ADJUST_XYZ))
            return NBTHelper.getVector3d(getBladeState(), ADJUST_XYZ);
        return Vec3.ZERO;
    }

    @Override
    public void setAdjust(Vec3 adjust) {
        updateBladeState(tag -> tag.put(ADJUST_XYZ, NBTHelper.newDoubleNBTList(adjust)));
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
            updateBladeState(tag -> tag.putString(TEXTURE_NAME, texture.toString()));
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
            updateBladeState(tag -> tag.putString(MODEL_NAME, model.toString()));
        }
    }

    @Override
    public int getTargetEntityId() {
        return getBladeState().getInt(TARGET_ENTITY);
    }

    @Override
    public void setTargetEntityId(int id) {
        updateBladeState(tag -> tag.putInt(TARGET_ENTITY, id));
    }

    @Override
    public ResourceLocation getComboRoot() {
        String comboRoot = getBladeState().getString(COMBO_ROOT);
        if (comboRoot.isEmpty())
            return ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
        ResourceLocation location = ResourceLocation.tryParse(comboRoot);
        return location != null && ComboStateRegistry.COMBO_STATE.containsKey(location) ? location : ComboStateRegistry.getId((ComboStateRegistry.STANDBY));
    }

    @Override
    public void setComboRoot(ResourceLocation rootLoc) {
        if (ComboStateRegistry.COMBO_STATE.containsKey(rootLoc)) {
            updateBladeState(tag -> tag.putString(COMBO_ROOT, rootLoc.toString()));
        } else {
            ResourceLocation id = ComboStateRegistry.getId(ComboStateRegistry.STANDBY);
            updateBladeState(tag -> tag.putString(COMBO_ROOT, id.toString()));
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
        updateBladeState(tag -> tag.putInt(MAX_DAMAGE, damage));
    }

    @Override
    public int getDamage() {
        return getBladeState().getInt(DAMAGE);
    }

    @Override
    public void setDamage(int damage) {
        int count = Math.max(0, damage);
        updateBladeState(tag -> tag.putInt(DAMAGE, count));
    }

    @Override
    public int getProudSoulCount() {
        return getBladeState().getInt(PROUD_SOUL);
    }

    @Override
    public void setProudSoulCount(int psCount) {
        int count = Math.max(0, psCount);
        updateBladeState(tag -> tag.putInt(PROUD_SOUL, count));
    }

    @Override
    public Collection<ResourceLocation> getSpecialEffects() {
        Collection<ResourceLocation> result = new HashSet<>();
        getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).forEach(tag -> {
            ResourceLocation se = ResourceLocation.tryParse(tag.getAsString());
            if (se != null && SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se))
                result.add(se);
        });
        return result;
    }

    @Override
    public void setSpecialEffects(ListTag list) {
        updateBladeState(tag -> tag.put(SPECIAL_EFFECTS, list));
    }

    @Override
    public boolean addSpecialEffect(ResourceLocation se) {
        if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
            final var seTag = StringTag.valueOf(se.toString());
            updateBladeState(tag -> {
                if (!tag.contains(SPECIAL_EFFECTS)) {
                    tag.put(SPECIAL_EFFECTS, new ListTag());
                }
                tag.getList(SPECIAL_EFFECTS, Tag.TAG_STRING).add(seTag);
            });
            return getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).contains(seTag);
        }
        return false;
    }

    @Override
    public boolean removeSpecialEffect(ResourceLocation se) {
        final var seTag = StringTag.valueOf(se.toString());
        updateBladeState(tag -> tag.getList(SPECIAL_EFFECTS, Tag.TAG_STRING).remove(seTag));
        return !getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).contains(seTag);
    }

    @Override
    public boolean hasSpecialEffect(ResourceLocation se) {
        if (SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
            return getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).contains(StringTag.valueOf(se.toString()));
        }
        getBladeState().getList(SPECIAL_EFFECTS, Tag.TAG_STRING).remove(StringTag.valueOf(se.toString()));
        return false;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public void setNonEmpty() {
        this.isEmpty = false;
    }
}
