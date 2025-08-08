package mods.flammpfeil.slashblade.capability.slashblade;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBladeDetune;

@SuppressWarnings("UnstableApiUsage")
public class CapabilitySlashBlade implements ItemComponentInitializer {
    public static final ComponentKey<SlashBladeState> BLADESTATE = ComponentRegistry.getOrCreate(SlashBlade.prefix("blade_state"), SlashBladeState.class);

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(item -> item instanceof ItemSlashBlade && !(item instanceof ItemSlashBladeDetune), BLADESTATE, SlashBladeState::new);
        registry.register(item -> item instanceof ItemSlashBladeDetune, BLADESTATE, stack -> {
            ItemSlashBladeDetune item = (ItemSlashBladeDetune) stack.getItem();
            return new SimpleSlashBladeState(
                    stack, item.getModel(), item.getTexture(), item.getBaseAttack(), item.getTier().getUses()
            );
        });
    }
}