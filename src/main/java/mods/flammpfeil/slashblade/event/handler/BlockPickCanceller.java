package mods.flammpfeil.slashblade.event.handler;

import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.SlashBladeKeyMappings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class BlockPickCanceller {
    private static final class SingletonHolder {
        private static final BlockPickCanceller instance = new BlockPickCanceller();
    }

    public static BlockPickCanceller getInstance() {
        return SingletonHolder.instance;
    }

    public void register() {
        // InputEvent.InteractionKeyMappingTriggered.CLICK_INPUT_CALLBACK.register(this::onBlockPick);
    }

    public static void onBlockPick(AtomicBoolean canceled) {
//        if (!event.isPickBlock())
//            return;

        final Minecraft instance = Minecraft.getInstance();
        LocalPlayer player = instance.player;
        if (player == null)
            return;
        if (KeyBindingHelper.getBoundKeyOf(SlashBladeKeyMappings.KEY_SUMMON_BLADE) != SlashBladeKeyMappings.KEY_SUMMON_BLADE.getDefaultKey())
            return;
        if (CapabilitySlashBlade.BLADESTATE.maybeGet(player.getMainHandItem()).isPresent()) {
            // event.setCanceled(true);
            canceled.set(true);
        }
    }
}
