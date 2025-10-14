package cn.sh1rocu.slashblade.api.event;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.ResourceLocation;

public class BaseEvent {
    protected boolean isCanceled = false;

    public static final ResourceLocation HIGHEST = SlashBlade.prefix("event_highest_priority");
    public static final ResourceLocation HIGH = SlashBlade.prefix("event_high_priority");
    public static final ResourceLocation LOW = SlashBlade.prefix("event_low_priority");
    public static final ResourceLocation LOWEST = SlashBlade.prefix("event_lowest_priority");
}
