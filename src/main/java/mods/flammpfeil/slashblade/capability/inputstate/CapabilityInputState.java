package mods.flammpfeil.slashblade.capability.inputstate;

import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;

public class CapabilityInputState implements EntityComponentInitializer {

    public static final ComponentKey<IInputState> INPUT_STATE = ComponentRegistry.getOrCreate(SlashBlade.prefix("input_state"), IInputState.class);

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, INPUT_STATE, entity -> new InputState());
    }
}