package cn.sh1rocu.slashblade.util;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.advancement.SlashBladeItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemPredicateRegistry {
    public static void init() {

    }

    public static final DataComponentPredicate.Type<SlashBladeItemPredicate> SLASHBLADE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE,
            SlashBlade.prefix("slashblade"),
            new DataComponentPredicate.ConcreteType<>(SlashBladeItemPredicate.CODEC)
    );
}