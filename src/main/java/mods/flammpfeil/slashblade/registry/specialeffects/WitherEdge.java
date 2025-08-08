package mods.flammpfeil.slashblade.registry.specialeffects;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeBaseEvent;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class WitherEdge extends SpecialEffect {

    public WitherEdge() {
        super(20, true, true);
    }

    public static void onSlashBladeUpdate(SlashBladeBaseEvent.UpdateBaseEvent event) {
        ISlashBladeState state = event.getSlashBladeState();
        if (state.hasSpecialEffect(SpecialEffectsRegistry.SPECIAL_EFFECT.getKey(SpecialEffectsRegistry.WITHER_EDGE))) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            if (!event.isSelected())
                return;

            Player player = (Player) event.getEntity();

            int level = player.experienceLevel;

            if (!SpecialEffect.isEffective(SpecialEffectsRegistry.SPECIAL_EFFECT.getKey(SpecialEffectsRegistry.WITHER_EDGE), level))
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
        }
    }

    public static void onSlashBladeHit(SlashBladeBaseEvent.HitBaseEvent event) {
        ISlashBladeState state = event.getSlashBladeState();
        if (state.hasSpecialEffect(SpecialEffectsRegistry.SPECIAL_EFFECT.getKey(SpecialEffectsRegistry.WITHER_EDGE))) {
            if (!(event.getUser() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getUser();

            int level = player.experienceLevel;

            if (SpecialEffect.isEffective(SpecialEffectsRegistry.SPECIAL_EFFECT.getKey(SpecialEffectsRegistry.WITHER_EDGE), level))
                event.getTarget().addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
        }
    }
}
