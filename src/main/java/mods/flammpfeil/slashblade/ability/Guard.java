package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.inputstate.IInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class Guard {
    private static final class SingletonHolder {
        private static final Guard instance = new Guard();
    }

    public static Guard getInstance() {
        return SingletonHolder.instance;
    }

    private Guard() {
    }

    public void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::onLivingAttack);
    }

    public static final Identifier ADVANCEMENT_GUARD = Identifier.fromNamespaceAndPath(SlashBlade.MODID, "abilities/guard");
    public static final Identifier ADVANCEMENT_GUARD_JUST = Identifier.fromNamespaceAndPath(SlashBlade.MODID,
            "abilities/guard_just");

    final static EnumSet<InputCommand> move = EnumSet.of(InputCommand.FORWARD, InputCommand.BACK, InputCommand.LEFT,
            InputCommand.RIGHT);

    public boolean onLivingAttack(LivingEntity victim, DamageSource source, float amount) {
        // begin executable check -----------------
        // item check
        ItemStack stack = victim.getMainHandItem();
        Optional<ISlashBladeState> slashBlade = CapabilitySlashBlade.getBladeState(stack);
        if (slashBlade.isEmpty())
            return true;
        if (slashBlade.filter(ISlashBladeState::isBroken).isPresent())
            return true;
        var thorns = victim.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.THORNS);
        if (EnchantmentHelper.getItemEnchantmentLevel(thorns, stack) <= 0)
            return true;

        // user check
        if (!victim.onGround())
            return true;
        Optional<IInputState> input = CapabilityInputState.INPUT_STATE.maybeGet(victim);
        if (input.isEmpty())
            return true;

        // commanc check
        InputCommand targetCommand = InputCommand.SNEAK;
        boolean handleCommand = input.filter(i -> i.getCommands().contains(targetCommand)
                && i.getCommands().stream().noneMatch(move::contains)).isPresent();

        if (handleCommand)
            AdvancementHelper.grantCriterion(victim, ADVANCEMENT_GUARD);

        // ninja run
        handleCommand |= (input.filter(i -> i.getCommands().contains(InputCommand.SPRINT)).isPresent()
                && victim.isSprinting());

        if (!handleCommand)
            return true;

        // range check
        if (!isInsideGuardableRange(source, victim))
            return true;

        // performance branch -----------------
        // just check
        long timeStartPress = input.map(i -> {
            Long l = i.getLastPressTime(targetCommand);
            return l == null ? 0 : l;
        }).get();
        long timeCurrent = victim.level().getGameTime();

        var soulSpeed = victim.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED);
        int soulSpeedLevel = EnchantmentHelper.getEnchantmentLevel(soulSpeed, victim);
        int justAcceptancePeriod = 5 + soulSpeedLevel;

        boolean isJust = false;
        if (timeCurrent - timeStartPress < justAcceptancePeriod) {
            isJust = true;
            AdvancementHelper.grantedIf(Enchantments.SOUL_SPEED, victim);
        }

        // rank check
        boolean isHighRank = false;
        Optional<IConcentrationRank> rank = CapabilityConcentrationRank.RANK_POINT.maybeGet(victim);
        if (rank.filter(r -> IConcentrationRank.ConcentrationRanks.S.level <= r.getRank(timeCurrent).level).isPresent())
            isHighRank = true;

        // damage sauce check
        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE)
                || source.getDirectEntity() instanceof Projectile;

        // after executable check -----------------
        if (!isJust) {
            if (!isProjectile)
                return true;
            if (!isHighRank && source.is(DamageTypeTags.BYPASSES_ARMOR))
                return true;

            boolean inMotion = slashBlade.filter(s -> {
                Identifier current = s.resolvCurrentComboState(victim);
                ComboState currentCS = ComboStateRegistry.COMBO_STATE.getValue(current);
                return !current.equals(ComboStateRegistry.getId(ComboStateRegistry.NONE)) && current.equals(currentCS.getNext(victim));
            }).isPresent();
            if (inMotion)
                return true;
        } else {
            if (!isProjectile && !(source.getDirectEntity() instanceof LivingEntity))
                return true;
        }

        boolean canceled = false;

        // execute performance------------------
        // damage cancel
        canceled = true;

        // Motion
        if (isJust) {
            slashBlade.ifPresent(s -> s.updateComboSeq(victim, ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1)));
        } else {
            slashBlade.ifPresent(s -> s.updateComboSeq(victim, ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1_END2)));
        }

        // DirectAttack knockback
        if (!isProjectile) {
            Entity entity = source.getDirectEntity();
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).knockback(0.5D, entity.getX() - victim.getX(), entity.getZ() - victim.getZ());
            }
        }

        // untouchable time
        if (isJust)
            Untouchable.setUntouchable(victim, 10);

        // rankup
        if (isJust)
            rank.ifPresent(r -> r.addRankPoint(victim.level().damageSources().thorns(victim)));

        // play sound
        if (victim instanceof Player) {
            victim.playSound(SoundEvents.TRIDENT_HIT_GROUND, 1.0F,
                    1.0F + victim.level().getRandom().nextFloat() * 0.4F);
        }

        // advancement
        if (isJust)
            AdvancementHelper.grantCriterion(victim, ADVANCEMENT_GUARD_JUST);

        // cost-------------------------
        if (!isJust && !isHighRank && victim.level() instanceof ServerLevel serverLevel) {
            slashBlade.ifPresent(s -> {
                var serverPlayer = victim instanceof ServerPlayer sp ? sp : null;
                stack.hurtAndBreak(1, serverLevel, serverPlayer, ItemSlashBlade.getOnBroken(stack, victim));
            });
        }

        return !canceled;
    }

    public boolean isInsideGuardableRange(DamageSource source, LivingEntity victim) {
        Vec3 sPos = source.getSourcePosition();
        if (sPos != null) {
            Vec3 viewVec = victim.getViewVector(1.0F);
            Vec3 attackVec = sPos.vectorTo(victim.position()).normalize();
            attackVec = new Vec3(attackVec.x, 0.0D, attackVec.z);
            return attackVec.dot(viewVec) < 0.0D;
        }
        return false;
    }
}
