package mods.flammpfeil.slashblade.event.bladestand;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BlandStandEventHandler {
    private static final ResourceLocation LOWEST = SlashBlade.prefix("lowest_priority");

    public static void init() {
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.addPhaseOrdering(Event.DEFAULT_PHASE, LOWEST);

        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(BlandStandEventHandler::eventKoseki);
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(BlandStandEventHandler::eventChangeSE);
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(BlandStandEventHandler::eventChangeSA);
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(BlandStandEventHandler::eventCopySE);
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(BlandStandEventHandler::eventCopySA);
        SlashBladeBaseEvent.BLADE_STAND_ATTACK.register(LOWEST, BlandStandEventHandler::eventProudSoulEnchantment);
        PreCopySpecialAttackFromBladeBaseEvent.CALLBACK.register(BlandStandEventHandler::copySAEnchantmentCheck);
        ProudSoulEnchantmentBaseEvent.CALLBACK.register(BlandStandEventHandler::proudSoulEnchantmentProbabilityCheck);
    }

    public static void eventKoseki(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        var slashBladeDefinitionRegistry = SlashBlade.getSlashBladeDefinitionRegistry(event.getBladeStand().level());
        if (!slashBladeDefinitionRegistry.containsKey(SlashBladeBuiltInRegistry.KOSEKI.location())) {
            return;
        }
        if (!(event.getDamageSource().getEntity() instanceof WitherBoss)) {
            return;
        }
        if (!event.getDamageSource().is(DamageTypeTags.IS_EXPLOSION)) {
            return;
        }
        var in = SlashBladeIngredient.of(RequestDefinition.Builder.newInstance().build());
        if (!in.test(event.getBlade())) {
            return;
        }
        event.getBladeStand().setItem(Objects.requireNonNull(slashBladeDefinitionRegistry.get(SlashBladeBuiltInRegistry.KOSEKI)).getBlade());
    }

    public static void eventChangeSE(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();
        if (blade.isEmpty()) {
            return;
        }
        if (!stack.is(SlashBladeItemTags.CAN_CHANGE_SE)) {
            return;
        }
        var world = player.level();
        var state = event.getSlashBladeState();

        if (stack.getTag() == null) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag.contains("SpecialEffectType")) {
            var bladeStand = event.getBladeStand();
            ResourceLocation SEKey = new ResourceLocation(tag.getString("SpecialEffectType"));
            if (!(SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(SEKey))) {
                return;
            }
            if (state.hasSpecialEffect(SEKey)) {
                return;
            }

            BladeChangeSpecialEffectBaseEvent e = new BladeChangeSpecialEffectBaseEvent(
                    blade, state, SEKey, event);

            if (!player.isCreative()) {
                e.setShrinkCount(1);
            }

            BladeChangeSpecialEffectBaseEvent.CALLBACK.invoker().onChangeSpecialEffect(e);
            if (e.isCanceled()) {
                return;
            }

            if (stack.getCount() < e.getShrinkCount()) {
                return;
            }

            state.addSpecialEffect(e.getSEKey());

            RandomSource random = player.getRandom();

            spawnSucceedEffects(world, bladeStand, random);

            stack.shrink(e.getShrinkCount());

            event.setCanceled(true);
        }
    }

    public static void eventChangeSA(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        CompoundTag tag = stack.getTag();

        if (!stack.is(SlashBladeItemTags.CAN_CHANGE_SA) || tag == null || !tag.contains("SpecialAttackType")) {
            return;
        }

        ResourceLocation SAKey = new ResourceLocation(tag.getString("SpecialAttackType"));
        if (!SlashArtsRegistry.SLASH_ARTS.containsKey(SAKey)) {
            return;
        }

        ItemStack blade = event.getBlade();

        CapabilitySlashBlade.BLADESTATE.maybeGet(blade).ifPresent(state -> {
            if (!SAKey.equals(state.getSlashArtsKey())) {

                BladeChangeSpecialAttackBaseEvent e = new BladeChangeSpecialAttackBaseEvent(
                        blade, state, SAKey, event);

                if (!player.isCreative()) {
                    e.setShrinkCount(1);
                }

                BladeChangeSpecialAttackBaseEvent.CALLBACK.invoker().onChangeSpecialAttack(e);
                if (e.isCanceled()) {
                    return;
                }

                if (stack.getCount() < e.getShrinkCount()) {
                    return;
                }

                state.setSlashArtsKey(e.getSAKey());

                RandomSource random = player.getRandom();
                BladeStandEntity bladeStand = event.getBladeStand();

                spawnSucceedEffects(player.level(), bladeStand, random);

                stack.shrink(e.getShrinkCount());
            }
        });
        event.setCanceled(true);
    }

    public static void eventCopySE(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();
        if (blade.isEmpty()) {
            return;
        }
        if (!stack.is(SlashBladeItemTags.CAN_COPY_SE)) {
            return;
        }

        CompoundTag crystalTag = stack.getTag();
        if (crystalTag != null && crystalTag.contains("SpecialEffectType")) {
            return;
        }

        var world = player.level();
        var state = event.getSlashBladeState();
        var bladeStand = event.getBladeStand();
        var specialEffects = state.getSpecialEffects();

        for (var se : specialEffects) {
            if (!SpecialEffectsRegistry.SPECIAL_EFFECT.containsKey(se)) {
                continue;
            }

            PreCopySpecialEffectFromBladeBaseEvent pe = new PreCopySpecialEffectFromBladeBaseEvent(
                    blade, state, se, event, Objects.requireNonNull(SpecialEffectsRegistry.SPECIAL_EFFECT.get(se)).isRemovable(),
                    Objects.requireNonNull(SpecialEffectsRegistry.SPECIAL_EFFECT.get(se)).isCopiable());

            if (!player.isCreative()) {
                pe.setShrinkCount(1);
            }

            PreCopySpecialEffectFromBladeBaseEvent.CALLBACK.invoker().onPreCopySpecialEffect(pe);
            if (pe.isCanceled()) {
                return;
            }

            if (stack.getCount() < pe.getShrinkCount()) {
                continue;
            }

            if (!pe.isCopiable()) {
                continue;
            }

            ItemStack orb = new ItemStack(SBItems.proudsoul_crystal);
            CompoundTag tag = new CompoundTag();
            tag.putString("SpecialEffectType", se.toString());
            orb.setTag(tag);

            stack.shrink(pe.getShrinkCount());

            RandomSource random = player.getRandom();

            spawnSucceedEffects(world, bladeStand, random);

            ItemEntity itemEntity = player.drop(orb, true);

            if (pe.isRemovable()) {
                state.removeSpecialEffect(se);
            }

            CopySpecialEffectFromBladeBaseEvent e = new CopySpecialEffectFromBladeBaseEvent(
                    pe, orb, itemEntity);

            CopySpecialEffectFromBladeBaseEvent.CALLBACK.invoker().onCopySpecialEffect(e);

            event.setCanceled(true);
            return;
        }
    }

    public static void eventCopySA(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();
        if (blade.isEmpty()) {
            return;
        }
        if (!stack.is(SlashBladeItemTags.CAN_COPY_SA) || !stack.isEnchanted()) {
            return;
        }
        var world = player.level();
        var state = event.getSlashBladeState();
        var bladeStand = event.getBladeStand();
        ResourceLocation SA = state.getSlashArtsKey();
        if (SA != null && !SA.equals(SlashArtsRegistry.SLASH_ARTS.getKey(SlashArtsRegistry.NONE))) {

            PreCopySpecialAttackFromBladeBaseEvent pe = new PreCopySpecialAttackFromBladeBaseEvent(
                    blade, state, SA, event);

            if (!player.isCreative()) {
                pe.setShrinkCount(1);
            }

            PreCopySpecialAttackFromBladeBaseEvent.CALLBACK.invoker().onPreCopySpecialAttack(pe);
            if (pe.isCanceled()) {
                return;
            }

            if (stack.getCount() < pe.getShrinkCount()) {
                return;
            }

            ItemStack orb = new ItemStack(SBItems.proudsoul_sphere);
            CompoundTag tag = new CompoundTag();
            tag.putString("SpecialAttackType", state.getSlashArtsKey().toString());
            orb.setTag(tag);

            stack.shrink(pe.getShrinkCount());

            RandomSource random = player.getRandom();

            spawnSucceedEffects(world, bladeStand, random);

            ItemEntity itemEntity = player.drop(orb, true);

            CopySpecialAttackFromBladeBaseEvent e = new CopySpecialAttackFromBladeBaseEvent(
                    pe, orb, itemEntity);

            CopySpecialAttackFromBladeBaseEvent.CALLBACK.invoker().onCopySpecialAttack(e);

            event.setCanceled(true);
        }
    }

    public static void eventProudSoulEnchantment(SlashBladeBaseEvent.BladeStandAttackBaseEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();

        if (blade.isEmpty()) {
            return;
        }

        if (!stack.is(SlashBladeItemTags.PROUD_SOULS)) {
            return;
        }

        if (!stack.isEnchanted()) {
            return;
        }

        var world = player.level();
        var random = world.getRandom();
        var bladeStand = event.getBladeStand();
        Map<Enchantment, Integer> currentBladeEnchantments = EnchantmentHelper.getEnchantments(blade);
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        AtomicInteger totalShrinkCount = new AtomicInteger(0);
        if (!player.isCreative()) {
            totalShrinkCount.set(1);
        }
        EnchantmentHelper.getEnchantments(stack).forEach((enchantment, level) -> {
            if (event.isCanceled()) {
                return;
            }
            // if (!blade.canApplyAtEnchantingTable(enchantment)) {
            if (!enchantment.canEnchant(blade)) {
                return;
            }

            var probability = 1.0F;
            if (stack.is(SBItems.proudsoul_tiny)) {
                probability = 0.25F;
            }
            if (stack.is(SBItems.proudsoul)) {
                probability = 0.5F;
            }
            if (stack.is(SBItems.proudsoul_ingot)) {
                probability = 0.75F;
            }

            int enchantLevel = Math.min(enchantment.getMaxLevel(),
                    EnchantmentHelper.getItemEnchantmentLevel(enchantment, blade) + 1);

            ProudSoulEnchantmentBaseEvent e = new ProudSoulEnchantmentBaseEvent(
                    blade, event.getSlashBladeState(), enchantment, enchantLevel, false, probability,
                    totalShrinkCount.get(), event);

            ProudSoulEnchantmentBaseEvent.CALLBACK.invoker().onProudSoulEnchantment(e);
            if (e.isCanceled()) {
                return;
            }

            totalShrinkCount.set(e.getTotalShrinkCount());

            enchantments.put(e.getEnchantment(), e.getEnchantLevel());

            if (!e.willTryNextEnchant()) {
                event.setCanceled(true);
            }
        });

        if (stack.getCount() < totalShrinkCount.get()) {
            return;
        }
        stack.shrink(totalShrinkCount.get());

        currentBladeEnchantments.putAll(enchantments);
        EnchantmentHelper.setEnchantments(currentBladeEnchantments, blade);

        if (!enchantments.isEmpty()) {
            spawnSucceedEffects(world, bladeStand, random);
        }

        event.setCanceled(true);
    }


    public static void copySAEnchantmentCheck(PreCopySpecialAttackFromBladeBaseEvent event) {
        SlashBladeBaseEvent.BladeStandAttackBaseEvent oriEvent = event.getOriginalEvent();
        if (oriEvent == null) {
            return;
        }
        Player player = (Player) oriEvent.getDamageSource().getEntity();
        if (player != null) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

            ItemStack blade = event.getBlade();
            Set<Enchantment> enchantments = EnchantmentHelper.getEnchantments(stack).keySet();
            boolean flag = false;
            for (Enchantment e : enchantments) {
                if (EnchantmentHelper.getItemEnchantmentLevel(e, blade) >= e.getMaxLevel()) {
                    flag = true;
                }
            }
            if (!flag) {
                event.setCanceled(true);
            }
        }
    }

    public static void proudSoulEnchantmentProbabilityCheck(ProudSoulEnchantmentBaseEvent event) {
        SlashBladeBaseEvent.BladeStandAttackBaseEvent oriEvent = event.getOriginalEvent();
        if (oriEvent == null) {
            return;
        }
        Player player = (Player) oriEvent.getDamageSource().getEntity();
        if (player != null) {
            Level world = player.level();
            RandomSource random = world.getRandom();

            if (random.nextFloat() > event.getProbability()) {
                event.setCanceled(true);
            }
        }
    }

    private static void spawnSucceedEffects(Level world, BladeStandEntity bladeStand, RandomSource random) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        // 音效
        serverLevel.playSound(
                bladeStand,
                bladeStand.getPos(),
                SoundEvents.WITHER_SPAWN,
                SoundSource.BLOCKS,
                0.5f,
                0.8f
        );

        // 粒子效果
        for (int i = 0; i < 32; ++i) {
            double xDist = (random.nextFloat() * 2.0F - 1.0F);
            double yDist = (random.nextFloat() * 2.0F - 1.0F);
            double zDist = (random.nextFloat() * 2.0F - 1.0F);
            if (xDist * xDist + yDist * yDist + zDist * zDist <= 1.0D) {
                double x = bladeStand.getX(xDist / 4.0D);
                double y = bladeStand.getY(0.5D + yDist / 4.0D);
                double z = bladeStand.getZ(zDist / 4.0D);
                serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        x, y, z,
                        0,
                        xDist, yDist + 0.2D, zDist,
                        1);
            }
        }
    }
}
