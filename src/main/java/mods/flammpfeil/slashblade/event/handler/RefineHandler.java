package mods.flammpfeil.slashblade.event.handler;

import cn.sh1rocu.slashblade.api.event.AnvilRepairEvent;
import cn.sh1rocu.slashblade.api.event.AnvilUpdateEvent;
import cn.sh1rocu.slashblade.api.extension.BaseItemExtension;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.event.RefineProgressEvent;
import mods.flammpfeil.slashblade.event.RefineSettlementEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

import java.util.concurrent.atomic.AtomicInteger;

public class RefineHandler {
    private static final class SingletonHolder {
        private static final RefineHandler instance = new RefineHandler();
    }

    public static RefineHandler getInstance() {
        return SingletonHolder.instance;
    }

    private RefineHandler() {
    }

    public void register() {
        AnvilUpdateEvent.CALLBACK.register(this::onAnvilUpdateEvent);
        AnvilRepairEvent.CALLBACK.register(this::onAnvilRepairEvent);
        RefineProgressEvent.CALLBACK.register(this::refineLimitCheck);
    }

    public void onAnvilUpdateEvent(AnvilUpdateEvent event) {
        if (!event.getOutput().isEmpty())
            return;

        ItemStack base = event.getLeft();
        ItemStack material = event.getRight();

        if (base.isEmpty()) {
            return;
        }
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(base).isEmpty()) {
            return;
        }

        if (material.isEmpty()) {
            return;
        }

        boolean isRepairable = base.getItem().isValidRepairItem(base, material);
        if (!isRepairable) {
            return;
        }

        int level = 0;
        if (material.getItem() instanceof BaseItemExtension extension)
            level = extension.getEnchantmentValue(material);
        else if (material.getItem() instanceof TieredItem tier)
            level = tier.getEnchantmentValue();

        if (level < 0) {
            return;
        }

        ItemStack result = base.copy();

        int refineLimit = Math.max(10, level);

        int materialCost = 0;
        int levelCostBase = SlashBladeConfig.REFINE_LEVEL_COST.get();
        int costResult = 0;
        AtomicInteger refineResult = new AtomicInteger(0);
        CapabilitySlashBlade.BLADESTATE.maybeGet(result).ifPresent(s -> refineResult.set(s.getRefine()));

        while (materialCost < material.getCount()) {

            RefineProgressEvent e = new RefineProgressEvent(result,
                    CapabilitySlashBlade.BLADESTATE.maybeGet(result).orElse(new SlashBladeState(result)), materialCost + 1, levelCostBase,
                    costResult, refineResult.get(), event);

            RefineProgressEvent.CALLBACK.invoker().onRefineProgress(e);
            if (e.isCanceled()) {
                break;
            }

            refineResult.set(e.getRefineResult());

            materialCost = e.getMaterialCost();
            costResult = e.getCostResult() + e.getLevelCost();

            if (!event.getPlayer().getAbilities().instabuild && event.getPlayer().experienceLevel < costResult) {
                break;
            }
        }

        if (CapabilitySlashBlade.BLADESTATE.maybeGet(result).isPresent()) {
            ISlashBladeState state = CapabilitySlashBlade.BLADESTATE.maybeGet(result).orElse(new SlashBladeState(result));
            RefineSettlementEvent e2 = new RefineSettlementEvent(result,
                    state, materialCost, costResult, refineResult.get(), event);

            RefineSettlementEvent.CALLBACK.invoker().onRefineSettlement(e2);
            if (e2.isCanceled()) {
                return;
            }
            materialCost = e2.getMaterialCost();
            costResult = e2.getCostResult();

            if (state.getRefine() <= refineLimit) {
                if (state.getRefine() + e2.getRefineResult() < 200) {
                    state.setMaxDamage(state.getMaxDamage() + e2.getRefineResult());
                } else if (state.getRefine() < 200) {
                    state.setMaxDamage(state.getMaxDamage() + Math.min(state.getRefine() + e2.getRefineResult(), 200)
                            - state.getRefine());
                }

                state.setProudSoulCount(state.getProudSoulCount() + getRefineProudsoulCount(level, state, e2));

                state.setRefine(e2.getRefineResult());
            }

            result.setDamageValue(result.getDamageValue() - Math.max(result.getDamageValue(), materialCost * Math.max(1, level / 2)));
        }

        event.setMaterialCost(materialCost);
        event.setCost(costResult);
        event.setOutput(result);
    }

    public int getRefineProudsoulCount(int level, ISlashBladeState state, RefineSettlementEvent e2) {
        return (e2.getRefineResult() - state.getRefine())
                * Math.min(5000, level * 10);
    }

    private static final ResourceLocation REFINE = new ResourceLocation(SlashBlade.MODID, "tips/refine");

    public void onAnvilRepairEvent(AnvilRepairEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer))
            return;

        ItemStack material = event.getRight();// .getIngredientInput();
        ItemStack base = event.getLeft();// .getItemInput();
        ItemStack output = event.getOutput();

        if (base.isEmpty())
            return;
        if (!(base.getItem() instanceof ItemSlashBlade))
            return;
        if (material.isEmpty())
            return;

        boolean isRepairable = base.getItem().isValidRepairItem(base, material);

        if (!isRepairable)
            return;

        int before = CapabilitySlashBlade.BLADESTATE.maybeGet(base).map(ISlashBladeState::getRefine).orElse(0);
        int after = CapabilitySlashBlade.BLADESTATE.maybeGet(output).map(ISlashBladeState::getRefine).orElse(0);

        if (before < after)
            AdvancementHelper.grantCriterion((ServerPlayer) event.getEntity(), REFINE);

    }

    public void refineLimitCheck(RefineProgressEvent event) {
        AnvilUpdateEvent oriEvent = event.getOriginalEvent();
        if (oriEvent == null) {
            return;
        }
        int level = 0;
        ItemStack material = oriEvent.getRight();
        if (material.getItem() instanceof BaseItemExtension extension)
            level = extension.getEnchantmentValue(material);
        else if (material.getItem() instanceof TieredItem tier)
            level = tier.getEnchantmentValue();
        int refineLimit = Math.max(10, level);
        if (event.getRefineResult() <= refineLimit) {
            event.setRefineResult(event.getRefineResult() + 1);
        }
    }
}
