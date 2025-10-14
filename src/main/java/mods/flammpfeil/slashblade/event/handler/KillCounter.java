package mods.flammpfeil.slashblade.event.handler;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingExperienceDropEvent;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.SwordType;
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
        LivingExperienceDropEvent.EVENT.register(this::onXPDropping);
    }

    public void onLivingDeathEvent(LivingEntity attacker, DamageSource source) {
        Entity trueSource = source.getEntity();

        if (!(trueSource instanceof LivingEntity))
            return;

        ItemStack stack = ((LivingEntity) trueSource).getMainHandItem();
        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return;

        CapabilitySlashBlade.getBladeState(stack).ifPresent(state -> {
            var killCountEvent = new SlashBladeEvent.AddKillCountEvent(stack, state, 1);
            SlashBladeEvent.ADD_KILL_COUNT.invoker().onAddAddKillCount(killCountEvent);
            state.setKillCount(state.getKillCount() + killCountEvent.getNewCount());
        });
    }

    public void onXPDropping(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player == null)
            return;
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty())
            return;
        if (CapabilitySlashBlade.getBladeState(stack).isEmpty())
            return;

        IConcentrationRank.ConcentrationRanks rankBonus = CapabilityConcentrationRank.RANK_POINT.maybeGet(player)
                .map(rp -> rp.getRank(player.getCommandSenderWorld().getGameTime()))
                .orElse(IConcentrationRank.ConcentrationRanks.NONE);
        int souls = (int) Math.floor(event.getDroppedExperience() * (1.0F + (rankBonus.level * 0.1F)));

        CapabilitySlashBlade.getBladeState(stack).ifPresent(state -> {
            var soulEvent = new SlashBladeEvent.AddProudSoulEvent(stack, state, Math.min(SlashBladeConfig.MAX_PROUD_SOUL_GOT.get(), souls));
            SlashBladeEvent.ADD_PROUD_SOUL.invoker().onAddProudSoul(soulEvent);
            int newCount = soulEvent.getNewCount();
            state.setProudSoulCount(
                    state.getProudSoulCount() + newCount);
            if (SwordType.from(stack).contains(SwordType.SOULEATER)) {
                int damage = Math.max(1, newCount / 4);
                stack.setDamageValue(Math.max(stack.getDamageValue() - damage, 0));
            }
        });
    }
}
