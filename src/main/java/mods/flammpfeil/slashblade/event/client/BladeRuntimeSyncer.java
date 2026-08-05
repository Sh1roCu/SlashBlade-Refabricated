package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;

public class BladeRuntimeSyncer {
    private static final class SingletonHolder {
        private static final BladeRuntimeSyncer instance = new BladeRuntimeSyncer();
    }

    public static BladeRuntimeSyncer getInstance() {
        return SingletonHolder.instance;
    }

    private BladeRuntimeSyncer() {
    }

    public void register() {
        BladeMotionEvent.CALLBACK.register(this::onBladeMotion);
    }

    public void onBladeMotion(BladeMotionEvent event) {
        if (!(event.getEntity().getMainHandItem().getItem() instanceof ItemSlashBlade)) {
            return;
        }

        CapabilitySlashBlade.getBladeState(event.getEntity().getMainHandItem()).ifPresent(state -> {
            state.setComboSeq(event.getCombo());
            state.setLastActionTime(event.getActionTime());
            ((EntityExtension) event.getEntity()).sb$getPersistentData().remove(ComboState.LAST_PROCESSED_TICK_KEY);
        });
    }
}