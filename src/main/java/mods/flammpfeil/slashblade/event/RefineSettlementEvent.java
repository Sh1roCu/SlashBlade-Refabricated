package mods.flammpfeil.slashblade.event;

import cn.sh1rocu.slashblade.api.event.AnvilUpdateEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class RefineSettlementEvent extends SlashBladeEvent implements ICancellableEvent {
    private final AnvilUpdateEvent originalEvent;
    private int materialCost;
    private int costResult;
    private int refineResult;
    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onRefineSettlement(event);
        }
    });

    public RefineSettlementEvent(ItemStack blade, ISlashBladeState state, int materialCost, int costResult,
                                 int refineResult, AnvilUpdateEvent originalEvent) {
        super(blade, state);
        this.materialCost = materialCost;
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

    public int getCostResult() {
        return costResult;
    }

    public int setCostResult(int costResult) {
        this.costResult = costResult;
        return this.costResult;
    }

    public int getRefineResult() {
        return refineResult;
    }

    public int setRefineResult(int refineResult) {
        this.refineResult = refineResult;
        return this.refineResult;
    }

    public interface Callback {
        void onRefineSettlement(RefineSettlementEvent event);
    }
}
