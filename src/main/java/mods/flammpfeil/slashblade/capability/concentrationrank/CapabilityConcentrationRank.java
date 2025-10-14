package mods.flammpfeil.slashblade.capability.concentrationrank;


import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CapabilityConcentrationRank implements EntityComponentInitializer {

    public static final ComponentKey<IConcentrationRank> RANK_POINT = ComponentRegistry.getOrCreate(SlashBlade.prefix("concentration"), IConcentrationRank.class);

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, RANK_POINT, entity -> new ConcentrationRank());
    }
}
