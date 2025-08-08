package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.LivingExperienceDropEvent;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class KillCounter {
    private static final class SingletonHolder {
        private static final KillCounter instance = new KillCounter();
    }

    public static KillCounter getInstance() {
        return SingletonHolder.instance;
    }

    private KillCounter() {
    }

    public void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingDeathEvent);
        LivingExperienceDropEvent.CALLBACK.register(this::onXPDropping);
    }

    public void onLivingDeathEvent(LivingEntity attacker, DamageSource source) {
        Entity trueSource = source.getEntity();

        if (!(trueSource instanceof LivingEntity))
            return;

        ItemStack stack = ((LivingEntity) trueSource).getMainHandItem();
        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(stack).isEmpty())
            return;

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(state ->
                state.setKillCount(state.getKillCount() + 1));
    }

    public void onXPDropping(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player == null)
            return;
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(stack).isEmpty())
            return;

        IConcentrationRank.ConcentrationRanks rankBonus = CapabilityConcentrationRank.RANK_POINT.maybeGet(player)
                .map(rp -> rp.getRank(player.getCommandSenderWorld().getGameTime()))
                .orElse(IConcentrationRank.ConcentrationRanks.NONE);
        int souls = (int) Math.floor(event.getDroppedExperience() * (1.0F + (rankBonus.level * 0.1F)));

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(
                state -> state.setProudSoulCount(
                        state.getProudSoulCount() + Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls)
                ));
    }
}
