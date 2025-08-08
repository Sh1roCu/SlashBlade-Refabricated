package mods.flammpfeil.slashblade.event;

import cn.sh1rocu.slashblade.api.event.AnvilUpdateEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class RefineProgressBaseEvent extends SlashBladeBaseEvent implements ICancellableEvent {
    private final AnvilUpdateEvent originalEvent;
    private int materialCost;
    private int levelCost;
    private final int costResult;
    private int refineResult;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onRefineProgress(event);
        }
    });

    public RefineProgressBaseEvent(ItemStack blade, ISlashBladeState state, int materialCost,
                                   int levelCost, int costResult, int refineResult, AnvilUpdateEvent originalEvent) {
        super(blade, state);
        this.materialCost = materialCost;
        this.levelCost = levelCost;
        this.costResult = costResult;
        this.refineResult = refineResult;
        this.originalEvent = originalEvent;
    }

    public @Nullable AnvilUpdateEvent getOriginalEvent() {
        return originalEvent;
    }

    public int getMaterialCost() {
        return materialCost;
    }

    public int setMaterialCost(int materialCost) {
        this.materialCost = materialCost;
        return this.materialCost;
    }

    public int getLevelCost() {
        return levelCost;
    }

    public int setLevelCost(int levelCost) {
        this.levelCost = levelCost;
        return this.levelCost;
    }

    public int getCostResult() {
        return costResult;
    }

    public int getRefineResult() {
        return refineResult;
    }

    public int setRefineResult(int refineResult) {
        this.refineResult = refineResult;
        return this.refineResult;
    }

    public interface Callback {
        void onRefineProgress(RefineProgressBaseEvent e);
    }
}
