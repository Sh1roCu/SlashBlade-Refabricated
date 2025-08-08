package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class LivingExperienceDropEvent extends LivingEvent implements ICancellableEvent {
    @Nullable
    private final Player attackingPlayer;
    private final int originalExperiencePoints;
    private int droppedExperiencePoints;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onExpDrop(event);
        }
    });

    public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
        super(entity);

        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    public int getDroppedExperience() {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperiencePoints = droppedExperience;
    }

    @Nullable
    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public int getOriginalExperience() {
        return originalExperiencePoints;
    }

    public interface Callback {
        void onExpDrop(LivingExperienceDropEvent event);
    }
}
