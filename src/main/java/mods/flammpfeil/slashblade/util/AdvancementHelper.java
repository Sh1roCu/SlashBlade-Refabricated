package mods.flammpfeil.slashblade.util;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class AdvancementHelper {

    public static final Identifier ADVANCEMENT_COMBO_A = SlashBlade.prefix("arts/combo_a");
    public static final Identifier ADVANCEMENT_COMBO_A_EX = SlashBlade.prefix("arts/combo_a_ex");
    public static final Identifier ADVANCEMENT_COMBO_B = SlashBlade.prefix("arts/combo_b");
    public static final Identifier ADVANCEMENT_COMBO_B_MAX = SlashBlade.prefix("arts/combo_b_max");
    public static final Identifier ADVANCEMENT_COMBO_C = SlashBlade.prefix("arts/combo_c");
    public static final Identifier ADVANCEMENT_AERIAL_A = SlashBlade.prefix("arts/aerial_a");
    public static final Identifier ADVANCEMENT_AERIAL_B = SlashBlade.prefix("arts/aerial_b");
    public static final Identifier ADVANCEMENT_UPPERSLASH = SlashBlade.prefix("arts/upperslash");
    public static final Identifier ADVANCEMENT_UPPERSLASH_JUMP = SlashBlade.prefix("arts/upperslash_jump");
    public static final Identifier ADVANCEMENT_AERIAL_CLEAVE = SlashBlade.prefix("arts/aerial_cleave");
    public static final Identifier ADVANCEMENT_RISING_STAR = SlashBlade.prefix("arts/rising_star");
    public static final Identifier ADVANCEMENT_RAPID_SLASH = SlashBlade.prefix("arts/rapid_slash");
    public static final Identifier ADVANCEMENT_JUDGEMENT_CUT = SlashBlade.prefix("arts/judgement_cut");
    public static final Identifier ADVANCEMENT_JUDGEMENT_CUT_JUST = SlashBlade.prefix("arts/judgement_cut_just");
    public static final Identifier ADVANCEMENT_QUICK_CHARGE = SlashBlade.prefix("arts/quick_charge");

    public static void grantCriterion(LivingEntity entity, Identifier resourcelocation) {
        if (entity instanceof ServerPlayer)
            grantCriterion((ServerPlayer) entity, resourcelocation);
    }

    public static void grantCriterion(ServerPlayer player, Identifier resourcelocation) {
        AdvancementHolder adv = player.level().getServer().getAdvancements().get(resourcelocation);
        if (adv == null)
            return;

        AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(adv);
        if (advancementprogress.isDone())
            return;

        for (String s : advancementprogress.getRemainingCriteria()) {
            player.getAdvancements().award(adv, s);
        }
    }

    static final Identifier EXEFFECT_ENCHANTMENT = SlashBlade.prefix("enchantment/");

    public static void grantedIf(ResourceKey<Enchantment> enchantment, LivingEntity owner) {
        Holder.Reference<Enchantment> holder = owner.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
        int level = EnchantmentHelper.getItemEnchantmentLevel(holder, owner.getMainHandItem());
        if (0 < level) {
            grantCriterion(owner, EXEFFECT_ENCHANTMENT.withSuffix("root"));
            grantCriterion(owner,
                    EXEFFECT_ENCHANTMENT.withSuffix(enchantment.identifier().getPath()));
        }
    }
}
