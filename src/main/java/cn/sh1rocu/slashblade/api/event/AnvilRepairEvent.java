package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AnvilRepairEvent extends PlayerEvent {
    private final ItemStack left;
    private final ItemStack right;
    private final ItemStack output;
    private float breakChance;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onAnvilRepair(event);
        }
    });

    public AnvilRepairEvent(Player player, ItemStack left, ItemStack right, ItemStack output) {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    public float getBreakChance() {
        return breakChance;
    }

    public void setBreakChance(float breakChance) {
        this.breakChance = breakChance;
    }

    public interface Callback {
        void onAnvilRepair(AnvilRepairEvent event);
    }
}
