package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static mods.flammpfeil.slashblade.SlashBlade.RegistryEvents.*;

public class SBEntityTypes {
    public static final EntityType<EntityAbstractSummonedSword> SummonedSword = register(SummonedSwordLoc, EntityType.Builder
            .of(EntityAbstractSummonedSword::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityStormSwords> StormSwords = register(StormSwordsLoc, EntityType.Builder
            .of(EntityStormSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntitySpiralSwords> SpiralSwords = register(SpiralSwordsLoc, EntityType.Builder
            .of(EntitySpiralSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityBlisteringSwords> BlisteringSwords = register(BlisteringSwordsLoc, EntityType.Builder
            .of(EntityBlisteringSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityHeavyRainSwords> HeavyRainSwords = register(HeavyRainSwordsLoc, EntityType.Builder
            .of(EntityHeavyRainSwords::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<EntityJudgementCut> JudgementCut = register(JudgementCutLoc, EntityType.Builder
            .of(EntityJudgementCut::new, MobCategory.MISC)
            .sized(2.5F, 2.5F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<BladeItemEntity> BladeItem = register(BladeItemEntityLoc, EntityType.Builder
            .of(BladeItemEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build());

    public static final EntityType<BladeStandEntity> BladeStand = register(BladeStandEntityLoc, EntityType.Builder
            .of(BladeStandEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .alwaysUpdateVelocity(false)
            .build());

    public static final EntityType<EntitySlashEffect> SlashEffect = register(SlashEffectLoc, EntityType.Builder
            .of(EntitySlashEffect::new, MobCategory.MISC)
            .sized(3.0F, 3.0F)
            .clientTrackingRange(4)
            .updateInterval(20).build());

    public static final EntityType<EntityDrive> Drive = register(DriveLoc, EntityType.Builder
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
