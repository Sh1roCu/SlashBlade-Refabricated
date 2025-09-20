package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.LivingAttackEvent;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class SlashBladeEventHandler {
    public static void onLivingOnFire(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();

        ItemStack stack = victim.getMainHandItem();
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack) <= 0)
            return;
        if (!source.is(DamageTypeTags.IS_FIRE))
            return;

        event.setCanceled(true);
    }

    public static void onLoadingBlade(SlashBladeRegistryEvent.Pre event) {
        if (!BuiltInRegistries.ITEM.containsKey(event.getSlashBladeDefinition().getItemName()))
            event.setCanceled(true);
    }
}
