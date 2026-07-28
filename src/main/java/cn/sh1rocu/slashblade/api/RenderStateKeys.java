package cn.sh1rocu.slashblade.api;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface RenderStateKeys {
    // LivingEntity
    RenderStateDataKey<Float> PERSISTENT_DATA_YROT = RenderStateDataKey.create(() -> "persistentData$yrot");
    RenderStateDataKey<Float> PERSISTENT_DATA_PREV_YROT = RenderStateDataKey.create(() -> "persistentData$prev_yrot");
    RenderStateDataKey<Boolean> IS_ALIVE = RenderStateDataKey.create(() -> "isAlive");
    RenderStateDataKey<Float> HP = RenderStateDataKey.create(() -> "health");
    RenderStateDataKey<Float> MAX_HP = RenderStateDataKey.create(() -> "maxHealth");
    RenderStateDataKey<Float> BB_HEIGHT = RenderStateDataKey.create(() -> "bbHeight");
    RenderStateDataKey<Vec3> POSITION = RenderStateDataKey.create(() -> "position");
    RenderStateDataKey<Double> COMBO_STATE_TIME = RenderStateDataKey.create(() -> "comboStateTime");
    RenderStateDataKey<ComboState> COMBO_STATE = RenderStateDataKey.create(() -> "comboState");
    RenderStateDataKey<Boolean> BLADE_IS_CHARGED = RenderStateDataKey.create(() -> "bladeIsCharged");
    RenderStateDataKey<ExtraEntityRenderData> EXTRA_ENTITY_RENDER_DATA = RenderStateDataKey.create(() -> "extraEntityRenderData");

    // Entity
    RenderStateDataKey<Integer> ENTITY_ID = RenderStateDataKey.create(() -> "entityId");
    RenderStateDataKey<Integer> TICK_COUNT = RenderStateDataKey.create(() -> "tickCount");

    // Player(Avatar)
    RenderStateDataKey<Integer> SELECTED_SLOT = RenderStateDataKey.create(() -> "selectedSlot");
    RenderStateDataKey<ItemStack> FIRST_INV_ITEM = RenderStateDataKey.create(() -> "firstInventoryItemStack");


    class ExtraEntityRenderData{
        public int fallFlyingTicks;
        public Vec3 viewVector;
        public Vec3 deltaMovement;

        public ExtraEntityRenderData(int fallFlyingTicks, Vec3 viewVector, Vec3 deltaMovement) {
            this.fallFlyingTicks = fallFlyingTicks;
            this.viewVector = viewVector;
            this.deltaMovement = deltaMovement;
        }
    }
}
