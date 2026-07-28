package mods.flammpfeil.slashblade.client;

import mods.flammpfeil.slashblade.client.renderer.LockonCircleRender;
import mods.flammpfeil.slashblade.client.renderer.gui.RankRenderer;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import mods.flammpfeil.slashblade.event.client.SneakingMotionCanceller;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClientHandler {
    public static void doClientStuff() {
        SneakingMotionCanceller.getInstance().register();

        if (FabricLoader.getInstance().isModLoaded("player_animation_library")) {
            PlayerAnimationOverrider.getInstance().register();
        } else {
            UserPoseOverrider.getInstance().register();
        }
        LockonCircleRender.getInstance().register();
        // TODO
        // AdvancementsRecipeRenderer.getInstance().register();


        RankRenderer.getInstance().register();

        registerKeyMapping();
    }

    public static void registerKeyMapping() {
        KeyMappingHelper.registerKeyMapping(SlashBladeKeyMappings.KEY_SPECIAL_MOVE);
        KeyMappingHelper.registerKeyMapping(SlashBladeKeyMappings.KEY_SUMMON_BLADE);
    }

    private static final Set<Item> blades = new HashSet<>() {{
        add(SBItems.SLASHBLADE);
        add(SBItems.SLASHBLADE_WHITE);
        add(SBItems.SLASHBLADE_WOOD);
        add(SBItems.SLASHBLADE_SILVERBAMBOO);
        add(SBItems.SLASHBLADE_BAMBOO);
    }};

    public static ItemModel baked(ItemModel bakedModel, ModelModifier.AfterBakeItem.Context context) {
        for (Item blade : blades) {
            Identifier modelLoc = BuiltInRegistries.ITEM.getKey(blade);
            Identifier id = context.itemId();
            if (id.equals(modelLoc)) {
                return bakeBlade(bakedModel, context.bakingContext());
            }
        }
        return bakedModel;
    }

    public static ItemModel bakeBlade(ItemModel bakedModel, ItemModel.BakingContext bakery) {
        return new BladeModel(bakedModel, bakery);
    }

    @SuppressWarnings("rawtypes,unchecked")
    public static void addLayers(
            EntityType<? extends LivingEntity> entityType,
            LivingEntityRenderer<?, ?, ?> entityRenderer,
            LivingEntityRenderLayerRegistrationCallback.RegistrationHelper registrationHelper,
            EntityRendererProvider.Context context) {

        registrationHelper.register(new LayerMainBlade<>((LivingEntityRenderer) entityRenderer));
    }
}
