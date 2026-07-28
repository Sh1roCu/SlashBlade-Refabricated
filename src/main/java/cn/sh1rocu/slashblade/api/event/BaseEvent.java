package cn.sh1rocu.slashblade.api.event;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.Identifier;

public class BaseEvent {
    protected boolean isCanceled = false;

    public static final Identifier HIGHEST = SlashBlade.prefix("event_highest_priority");
    public static final Identifier HIGH = SlashBlade.prefix("event_high_priority");
    public static final Identifier LOW = SlashBlade.prefix("event_low_priority");
    public static final Identifier LOWEST = SlashBlade.prefix("event_lowest_priority");
}
