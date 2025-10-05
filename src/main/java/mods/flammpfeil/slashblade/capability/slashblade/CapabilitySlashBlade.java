package mods.flammpfeil.slashblade.capability.slashblade;

import cn.sh1rocu.slashblade.api.extension.ISlashBladeCapabilityProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import mods.flammpfeil.slashblade.SlashBlade;

@SuppressWarnings("UnstableApiUsage")
public class CapabilitySlashBlade implements ItemComponentInitializer {
    public static final ComponentKey<ISlashBladeState> BLADESTATE = ComponentRegistry.getOrCreate(SlashBlade.prefix("blade_state"), ISlashBladeState.class);

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(item -> item instanceof ISlashBladeCapabilityProvider, BLADESTATE, itemStack -> {
            var blade = (ISlashBladeCapabilityProvider) itemStack.getItem();
            return blade.initCapability(itemStack);
        });
    }
}