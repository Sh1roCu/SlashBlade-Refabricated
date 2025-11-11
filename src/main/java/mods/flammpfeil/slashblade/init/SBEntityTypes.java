package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static mods.flammpfeil.slashblade.SlashBlade.RegistryEvents.*;

public class SBEntityTypes {
    public static final EntityType<EntityAbstractSummonedSword> SUMMONED_SWORD = register(SUMMONED_SWORD_LOC, EntityType.Builder
            .of(EntityAbstractSummonedSword::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityStormSwords> STORM_SWORDS = register(STORM_SWORDS_LOC, EntityType.Builder
            .of(EntityStormSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntitySpiralSwords> SPIRAL_SWORDS = register(SPIRAL_SWORDS_LOC, EntityType.Builder
            .of(EntitySpiralSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityBlisteringSwords> BLISTERING_SWORDS = register(BLISTERING_SWORDS_LOC, EntityType.Builder
            .of(EntityBlisteringSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityHeavyRainSwords> HEAVY_RAIN_SWORDS = register(HEAVY_RAIN_SWORDS_LOC, EntityType.Builder
            .of(EntityHeavyRainSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityJudgementCut> JUDGEMENT_CUT = register(JUDGEMENT_CUT_LOC, EntityType.Builder
            .of(EntityJudgementCut::new, MobCategory.MISC)
            .sized(2.5F, 2.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<BladeItemEntity> BLADE_ITEM = register(BLADE_ITEM_ENTITY_LOC, EntityType.Builder
            .of(BladeItemEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<BladeStandEntity> BLADE_STAND = register(BLADE_STAND_ENTITY_LOC, EntityType.Builder
            .of(BladeStandEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .alwaysUpdateVelocity(false)
            .build());

    public static final EntityType<EntitySlashEffect> SLASH_EFFECT = register(SLASH_EFFECT_LOC, EntityType.Builder
            .of(EntitySlashEffect::new, MobCategory.MISC)
            .sized(3.0F, 3.0F)
            .clientTrackingRange(4)
            .updateInterval(20).build());

    public static final EntityType<EntityDrive> DRIVE = register(DRIVE_LOC, EntityType.Builder
            .of(EntityDrive::new, MobCategory.MISC)
            .sized(3.0F, 3.0F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    private static <T extends EntityType<?>> T register(ResourceLocation loc, T type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, loc, type);
    }

    public static void init() {

    }
}
