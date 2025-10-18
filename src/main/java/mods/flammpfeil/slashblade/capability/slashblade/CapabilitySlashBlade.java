package mods.flammpfeil.slashblade.capability.slashblade;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

public class CapabilitySlashBlade {
    public static void init() {

    }

    public static final DataComponentType<CustomData> BLADESTATE_COMPONENT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, SlashBlade.prefix("blade_state"),
            DataComponentType.<CustomData>builder()
                    .persistent(CustomData.CODEC)
                    .networkSynchronized(CustomData.STREAM_CODEC)
                    .build()
    );

    public static Optional<ISlashBladeState> getBladeState(ItemStack stack) {
        if (stack.getItem() instanceof ItemSlashBlade blade) {
            return Optional.of(blade.initCapability(stack));
        }
        return Optional.empty();
    }
}