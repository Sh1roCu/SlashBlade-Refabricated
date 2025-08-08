package cn.sh1rocu.slashblade.mixin.accessor;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("scrollX")
    double sb$getScrollX();

    @Accessor("scrollY")
    double sb$getScrollY();

    @Accessor("widgets")
    Map<Advancement, AdvancementWidget> sb$getWidgets();
}
