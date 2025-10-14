package mods.flammpfeil.slashblade.client;

import cn.sh1rocu.slashblade.mixin.accessor.LivingEntityRendererAccessor;
import mods.flammpfeil.slashblade.client.renderer.LockonCircleRender;
import mods.flammpfeil.slashblade.client.renderer.gui.RankRenderer;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import mods.flammpfeil.slashblade.event.client.AdvancementsRecipeRenderer;
import mods.flammpfeil.slashblade.event.client.SneakingMotionCanceller;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.util.LoaderUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClientHandler {
    public static void doClientStuff() {
        SneakingMotionCanceller.getInstance().register();

        if (LoaderUtil.isClassAvailable("dev.kosmx.playerAnim.api.layered.AnimationStack")) {
            PlayerAnimationOverrider.getInstance().register();
        } else {
            UserPoseOverrider.getInstance().register();
        }
        LockonCircleRender.getInstance().register();
        AdvancementsRecipeRenderer.getInstance().register();


        RankRenderer.getInstance().register();

        ItemProperties.register(SBItems.slashblade, ResourceLocation.parse("slashblade:user"),
                (itemStack, level, livingEntity, i) -> {
                    BladeModel.user = livingEntity;
                    return 0;
                });

        ItemProperties.register(SBItems.slashblade_bamboo, ResourceLocation.parse("slashblade:user"),
                (itemStack, level, livingEntity, i) -> {
                    BladeModel.user = livingEntity;
                    return 0;
                });

        ItemProperties.register(SBItems.slashblade_silverbamboo, ResourceLocation.parse("slashblade:user"),
                (itemStack, level, livingEntity, i) -> {
                    BladeModel.user = livingEntity;
                    return 0;
                });

        ItemProperties.register(SBItems.slashblade_white, ResourceLocation.parse("slashblade:user"),
                (itemStack, level, livingEntity, i) -> {
                    BladeModel.user = livingEntity;
                    return 0;
                });

        ItemProperties.register(SBItems.slashblade_wood, ResourceLocation.parse("slashblade:user"),
                (itemStack, level, livingEntity, i) -> {
                    BladeModel.user = livingEntity;
                    return 0;
                });


        registerKeyMapping();
    }

    public static void registerKeyMapping() {
        KeyBindingHelper.registerKeyBinding(SlashBladeKeyMappings.KEY_SPECIAL_MOVE);
        KeyBindingHelper.registerKeyBinding(SlashBladeKeyMappings.KEY_SUMMON_BLADE);
    }

    private static final Set<Item> blades = new HashSet<>() {{
        add(SBItems.slashblade);
        add(SBItems.slashblade_white);
        add(SBItems.slashblade_wood);
        add(SBItems.slashblade_silverbamboo);
        add(SBItems.slashblade_bamboo);
    }};

    public static BakedModel Baked(BakedModel bakedModel, ModelModifier.AfterBake.Context context) {
        for (Item blade : blades) {
            ModelResourceLocation modelLoc = ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(blade));
            ModelResourceLocation id = context.topLevelId();
            if (id != null && id.equals(modelLoc)) {
                return bakeBlade(bakedModel, context.loader());
            }
        }
        return bakedModel;
    }

    public static BakedModel bakeBlade(BakedModel bakedModel, ModelBakery bakery) {
        return new BladeModel(bakedModel, bakery);
    }

    public static void addLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap, EntityRendererProvider.Context context) {
        addPlayerLayer(skinMap, PlayerSkin.Model.WIDE);
        addPlayerLayer(skinMap, PlayerSkin.Model.SLIM);

        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            addEntityLayer(renderers, type);
        }

//        addEntityLayer(renderers, EntityType.ZOMBIE);
//        addEntityLayer(renderers, EntityType.HUSK);
//        addEntityLayer(renderers, EntityType.ZOMBIE_VILLAGER);
//
//        addEntityLayer(renderers, EntityType.WITHER_SKELETON);
//        addEntityLayer(renderers, EntityType.SKELETON);
//        addEntityLayer(renderers, EntityType.STRAY);
//
//        addEntityLayer(renderers, EntityType.PIGLIN);
//        addEntityLayer(renderers, EntityType.PIGLIN_BRUTE);
//        addEntityLayer(renderers, EntityType.ZOMBIFIED_PIGLIN);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addPlayerLayer(Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap, PlayerSkin.Model skin) {
        EntityRenderer<? extends Player> renderer = skinMap.get(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            ((LivingEntityRendererAccessor) livingRenderer).sb$addLayer(new LayerMainBlade<>(livingRenderer));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addEntityLayer(Map<EntityType<?>, EntityRenderer<?>> renderers, EntityType type) {
        EntityRenderer<?> renderer = renderers.get(type);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            ((LivingEntityRendererAccessor) livingRenderer).sb$addLayer(new LayerMainBlade<>(livingRenderer));
        }
    }
}
