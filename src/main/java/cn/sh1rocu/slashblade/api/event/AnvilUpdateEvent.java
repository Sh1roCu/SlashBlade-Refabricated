package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnvilUpdateEvent extends BaseEvent implements ICancellableEvent {
    private final ItemStack left;
    private final ItemStack right;
    private final String name;
    private ItemStack output;
    private int cost;
    private int materialCost;
    private final Player player;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onAnvilUpdate(event);
        }
    });

    public AnvilUpdateEvent(ItemStack left, ItemStack right, String name, int cost, Player player) {
        this.left = left;
        this.right = right;
        this.output = ItemStack.EMPTY;
        this.name = name;
        this.player = player;
        this.setCost(cost);
        this.setMaterialCost(0);
    }

    public ItemStack getLeft() {
        return left;
    }

    public ItemStack getRight() {
        return right;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(int materialCost) {
        this.materialCost = materialCost;
    }

    public Player getPlayer() {
        return this.player;
    }

    public interface Callback {
        void onAnvilUpdate(AnvilUpdateEvent event);
    }
}
