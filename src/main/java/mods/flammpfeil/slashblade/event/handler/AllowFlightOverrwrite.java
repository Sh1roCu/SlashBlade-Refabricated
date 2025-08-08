package mods.flammpfeil.slashblade.event.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class AllowFlightOverrwrite {

    private static final class SingletonHolder {
        private static final AllowFlightOverrwrite instance = new AllowFlightOverrwrite();
    }

    public static AllowFlightOverrwrite getInstance() {
        return SingletonHolder.instance;
    }

    private AllowFlightOverrwrite() {
    }

    public void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onFMLServerAboutToStartEvent);
    }

    public void onFMLServerAboutToStartEvent(MinecraftServer server) {
        server.setFlightAllowed(true);
    }
}
