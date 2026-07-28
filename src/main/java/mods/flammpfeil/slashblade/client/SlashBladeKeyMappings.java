package mods.flammpfeil.slashblade.client;

import com.mojang.blaze3d.platform.InputConstants;
import mods.flammpfeil.slashblade.SlashBlade;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SlashBladeKeyMappings {
    public static KeyMapping.Category SLASH_BLADE = KeyMapping.Category.register(SlashBlade.prefix("slashblade"));

    public static final KeyMapping KEY_SPECIAL_MOVE = new KeyMapping("key.slashblade.special_move",
            /*KeyConflictContext.IN_GAME, KeyModifier.NONE, */InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V,
            SLASH_BLADE
    );

    public static final KeyMapping KEY_SUMMON_BLADE = new KeyMapping("key.slashblade.summon_blade",
            /*KeyConflictContext.IN_GAME, KeyModifier.NONE,*/ InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            SLASH_BLADE
    );
}
