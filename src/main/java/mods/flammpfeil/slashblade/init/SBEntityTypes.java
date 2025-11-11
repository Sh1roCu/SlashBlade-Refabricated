package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static mods.flammpfeil.slashblade.SlashBlade.RegistryEvents.*;

public class SBEntityTypes {
    public static final EntityType<EntityAbstractSummonedSword> SUMMONED_SWORD = register(SUMMONED_SWORD_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityAbstractSummonedSword::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityStormSwords> STORM_SWORDS = register(STORM_SWORDS_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityStormSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntitySpiralSwords> SPIRAL_SWORDS = register(SPIRAL_SWORDS_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntitySpiralSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityBlisteringSwords> BLISTERING_SWORDS = register(BLISTERING_SWORDS_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityBlisteringSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityHeavyRainSwords> HEAVY_RAIN_SWORDS = register(HEAVY_RAIN_SWORDS_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityHeavyRainSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityJudgementCut> JUDGEMENT_CUT = register(JUDGEMENT_CUT_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityJudgementCut::new)
            .dimensions(EntityDimensions.scalable(2.5F, 2.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<BladeItemEntity> BLADE_ITEM = register(BLADE_ITEM_ENTITY_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, BladeItemEntity::new)
            .dimensions(EntityDimensions.scalable(0.25F, 0.25F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<BladeStandEntity> BLADE_STAND = register(BLADE_STAND_ENTITY_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, BladeStandEntity::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(10)
            .trackedUpdateRate(20)
            .forceTrackedVelocityUpdates(false)
            .build());

    public static final EntityType<EntitySlashEffect> SLASH_EFFECT = register(SLASH_EFFECT_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntitySlashEffect::new)
            .dimensions(EntityDimensions.scalable(3.0F, 3.0F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20).build());

    public static final EntityType<EntityDrive> DRIVE = register(DRIVE_LOC, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityDrive::new)
            .dimensions(EntityDimensions.scalable(3.0F, 3.0F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    private static <T extends EntityType<?>> T register(ResourceLocation loc, T type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, loc, type);
    }

    public static void init() {

    }
}
