package cn.sh1rocu.slashblade.util;

import cn.sh1rocu.slashblade.api.event.LivingExperienceDropEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EventHooks {
    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        LivingExperienceDropEvent.CALLBACK.invoker().onExpDrop(event);
        if (event.isCanceled()) {
            return 0;
        }
        return event.getDroppedExperience();
    }
}