package mods.flammpfeil.slashblade;

import cn.sh1rocu.slashblade.api.event.LivingKnockBackEvent;
import com.google.common.base.CaseFormat;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.fabricators_of_create.porting_lib.util.ItemPredicateRegistry;
import mods.flammpfeil.slashblade.ability.*;
import mods.flammpfeil.slashblade.advancement.SlashBladeItemPredicate;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.BladeMotionEventBroadcaster;
import mods.flammpfeil.slashblade.event.handler.*;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.init.SBStatTypes;
import mods.flammpfeil.slashblade.network.NetworkManager;
import mods.flammpfeil.slashblade.recipe.RecipeSerializerRegistry;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.ModAttributes;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboCommands;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlashBlade {
    public static final String MODID = "slashblade";

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(SlashBlade.MODID, path);
    }

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, SlashBladeConfig.COMMON_CONFIG);

        SBItems.init();
        SBEntityTypes.init();
        SBStatTypes.init();

        setup();

        ModAttributes.init();
        NetworkManager.registerC2SPackets();

        ComboStateRegistry.init();
        SlashArtsRegistry.init();
        SlashBladeCreativeGroup.init();
        RecipeSerializerRegistry.init();

        SpecialEffectsRegistry.init();

        ItemPredicateRegistry.register(SlashBlade.prefix("slashblade"), SlashBladeItemPredicate::new);
    }

    private static void setup() {

        LivingKnockBackEvent.CALLBACK.register(KnockBackHandler::onLivingKnockBack);

        FallHandler.getInstance().register();
        LockOnManager.getInstance().register();
        Guard.getInstance().register();

        StunManager.init();

        RefineHandler.getInstance().register();
        KillCounter.getInstance().register();
        RankPointHandler.getInstance().register();
        AllowFlightOverrwrite.getInstance().register();
        BladeMotionEventBroadcaster.getInstance().register();

        InputCommandEvent.CALLBACK.register(TargetSelector::onInputChange);
        SummonedSwordArts.getInstance().register();
        SlayerStyleArts.getInstance().register();
        Untouchable.getInstance().register();
        EnemyStep.getInstance().register();
        KickJump.getInstance().register();
        SuperSlashArts.getInstance().register();

        ComboCommands.initDefaultStandByCommands();
    }

    public static class RegistryEvents {

        public static final ResourceLocation BLADE_ITEM_ENTITY_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(BladeItemEntity.class));

        public static final ResourceLocation BLADE_STAND_ENTITY_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(BladeStandEntity.class));

        public static final ResourceLocation SUMMONED_SWORD_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityAbstractSummonedSword.class));

        public static final ResourceLocation SPIRAL_SWORDS_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntitySpiralSwords.class));

        public static final ResourceLocation STORM_SWORDS_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityStormSwords.class));

        public static final ResourceLocation BLISTERING_SWORDS_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityBlisteringSwords.class));

        public static final ResourceLocation HEAVY_RAIN_SWORDS_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityHeavyRainSwords.class));

        public static final ResourceLocation JUDGEMENT_CUT_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityJudgementCut.class));

        public static final ResourceLocation SLASH_EFFECT_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntitySlashEffect.class));

        public static final ResourceLocation DRIVE_LOC = new ResourceLocation(SlashBlade.MODID,
                classToString(EntityDrive.class));

        private static String classToString(Class<? extends Entity> entityClass) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName())
                    .replace("entity_", "");
        }

        /**
         * /scoreboard objectives add stat minecraft.custom:slashblade.sword_summoned
         * /scoreboard objectives setdisplay sidebar stat
         */
    }

    public static Registry<SlashBladeDefinition> getSlashBladeDefinitionRegistry(Level level) {
        if (level.isClientSide())
            return BladeModelManager.getClientSlashBladeRegistry();
        return level.registryAccess().registryOrThrow(SlashBladeDefinition.REGISTRY_KEY);
    }

    public static HolderLookup.RegistryLookup<SlashBladeDefinition> getSlashBladeDefinitionRegistry(HolderLookup.Provider access) {
        return access.lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY);
    }
}
