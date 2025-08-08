package mods.flammpfeil.slashblade.capability.mobeffect;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CapabilityMobEffect implements EntityComponentInitializer {
    public static final ComponentKey<IMobEffectState> MOB_EFFECT = ComponentRegistry.getOrCreate(SlashBlade.prefix("mob_effect"), IMobEffectState.class);

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, MOB_EFFECT, entity -> new MobEffectState());
    }
}
