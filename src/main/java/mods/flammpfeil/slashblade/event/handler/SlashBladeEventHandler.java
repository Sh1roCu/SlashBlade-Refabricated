package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class SlashBladeEventHandler {
    public static boolean onLivingOnFire(LivingEntity victim, DamageSource source, float amount) {
        ItemStack stack = victim.getMainHandItem();
        var holder = victim.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_PROTECTION);
        if (EnchantmentHelper.getItemEnchantmentLevel(holder, stack) <= 0)
            return true;

        return !source.is(DamageTypeTags.IS_FIRE);
    }

    public static void onLoadingBlade(SlashBladeRegistryEvent.Pre event) {
        if (!BuiltInRegistries.ITEM.containsKey(event.getSlashBladeDefinition().getItemName()))
            event.setCanceled(true);
    }

    public static void onChargeBlade(SlashBladeEvent.ChargeActionEvent event) {
        var state = event.getSlashBladeState();
        var swordType = SwordType.from(event.getEntityLiving().getMainHandItem());
        if (state.isBroken() || state.isSealed() || !(swordType.contains(SwordType.ENCHANTED))) {
            event.setCanceled(true);
        }
    }
}
