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
    public static final EntityType<EntityAbstractSummonedSword> SUMMONED_SWORD = register(SummonedSwordLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityAbstractSummonedSword::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityStormSwords> STORM_SWORDS = register(StormSwordsLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityStormSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntitySpiralSwords> SPIRAL_SWORDS = register(SpiralSwordsLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntitySpiralSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityBlisteringSwords> BLISTERING_SWORDS = register(BlisteringSwordsLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityBlisteringSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityHeavyRainSwords> HEAVY_RAIN_SWORDS = register(HeavyRainSwordsLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityHeavyRainSwords::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<EntityJudgementCut> JUDGEMENT_CUT = register(JudgementCutLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntityJudgementCut::new)
            .dimensions(EntityDimensions.scalable(2.5F, 2.5F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<BladeItemEntity> BLADE_ITEM = register(BladeItemEntityLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, BladeItemEntity::new)
            .dimensions(EntityDimensions.scalable(0.25F, 0.25F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20)
            .build());

    public static final EntityType<BladeStandEntity> BLADE_STAND = register(BladeStandEntityLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, BladeStandEntity::new)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(10)
            .trackedUpdateRate(20)
            .forceTrackedVelocityUpdates(false)
            .build());

    public static final EntityType<EntitySlashEffect> SLASH_EFFECT = register(SlashEffectLoc, FabricEntityTypeBuilder
            .create(MobCategory.MISC, EntitySlashEffect::new)
            .dimensions(EntityDimensions.scalable(3.0F, 3.0F))
            .trackRangeChunks(4)
            .trackedUpdateRate(20).build());

    public static final EntityType<EntityDrive> DRIVE = register(DriveLoc, FabricEntityTypeBuilder
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
