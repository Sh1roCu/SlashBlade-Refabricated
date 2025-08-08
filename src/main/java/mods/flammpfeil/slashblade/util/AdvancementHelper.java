package mods.flammpfeil.slashblade.util;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class AdvancementHelper {

    public static final ResourceLocation ADVANCEMENT_COMBO_A = SlashBlade.prefix("arts/combo_a");
    public static final ResourceLocation ADVANCEMENT_COMBO_A_EX = SlashBlade.prefix("arts/combo_a_ex");
    public static final ResourceLocation ADVANCEMENT_COMBO_B = SlashBlade.prefix("arts/combo_b");
    public static final ResourceLocation ADVANCEMENT_COMBO_B_MAX = SlashBlade.prefix("arts/combo_b_max");
    public static final ResourceLocation ADVANCEMENT_COMBO_C = SlashBlade.prefix("arts/combo_c");
    public static final ResourceLocation ADVANCEMENT_AERIAL_A = SlashBlade.prefix("arts/aerial_a");
    public static final ResourceLocation ADVANCEMENT_AERIAL_B = SlashBlade.prefix("arts/aerial_b");
    public static final ResourceLocation ADVANCEMENT_UPPERSLASH = SlashBlade.prefix("arts/upperslash");
    public static final ResourceLocation ADVANCEMENT_UPPERSLASH_JUMP = SlashBlade.prefix("arts/upperslash_jump");
    public static final ResourceLocation ADVANCEMENT_AERIAL_CLEAVE = SlashBlade.prefix("arts/aerial_cleave");
    public static final ResourceLocation ADVANCEMENT_RISING_STAR = SlashBlade.prefix("arts/rising_star");
    public static final ResourceLocation ADVANCEMENT_RAPID_SLASH = SlashBlade.prefix("arts/rapid_slash");
    public static final ResourceLocation ADVANCEMENT_JUDGEMENT_CUT = SlashBlade.prefix("arts/judgement_cut");
    public static final ResourceLocation ADVANCEMENT_JUDGEMENT_CUT_JUST = SlashBlade.prefix("arts/judgement_cut_just");
    public static final ResourceLocation ADVANCEMENT_QUICK_CHARGE = SlashBlade.prefix("arts/quick_charge");

    public static void grantCriterion(LivingEntity entity, ResourceLocation resourcelocation) {
        if (entity instanceof ServerPlayer)
            grantCriterion((ServerPlayer) entity, resourcelocation);
    }

    public static void grantCriterion(ServerPlayer player, ResourceLocation resourcelocation) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(resourcelocation);
        if (adv == null)
            return;

        AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(adv);
        if (advancementprogress.isDone())
            return;

        for (String s : advancementprogress.getRemainingCriteria()) {
            player.getAdvancements().award(adv, s);
        }
    }

    static final ResourceLocation EXEFFECT_ENCHANTMENT = SlashBlade.prefix("enchantment/");

    public static void grantedIf(Enchantment enchantment, LivingEntity owner) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, owner.getMainHandItem());
        if (0 < level) {
            grantCriterion(owner, EXEFFECT_ENCHANTMENT.withSuffix("root"));
            grantCriterion(owner,
                    EXEFFECT_ENCHANTMENT.withSuffix(BuiltInRegistries.ENCHANTMENT.getKey(enchantment).getPath()));
        }
    }
}
