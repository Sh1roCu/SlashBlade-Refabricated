package cn.sh1rocu.slashblade.util;

import cn.sh1rocu.slashblade.api.event.InputEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;

public class ClientHooks {
    public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand) {
        InputEvent.InteractionKeyMappingTriggered event = new InputEvent.InteractionKeyMappingTriggered(button, keyBinding, hand);
        InputEvent.CLICK_INPUT_CALLBACK.invoker().onClickInput(event);
        return event;
    }
}
