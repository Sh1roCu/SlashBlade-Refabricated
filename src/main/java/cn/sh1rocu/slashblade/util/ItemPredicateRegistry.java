package cn.sh1rocu.slashblade.util;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.advancement.SlashBladeItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemPredicateRegistry {
    public static void init() {

    }

    public static ItemSubPredicate.Type<SlashBladeItemPredicate> SLASHBLADE = register("slashblade", SlashBladeItemPredicate.TYPE);

    private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String name, ItemSubPredicate.Type<T> type) {
        return Registry.register(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, SlashBlade.prefix(name), type);
    }
}