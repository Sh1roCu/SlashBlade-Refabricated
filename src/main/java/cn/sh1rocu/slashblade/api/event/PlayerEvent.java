package cn.sh1rocu.slashblade.api.event;

import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends LivingEvent {
    private final Player player;

    public PlayerEvent(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }
}