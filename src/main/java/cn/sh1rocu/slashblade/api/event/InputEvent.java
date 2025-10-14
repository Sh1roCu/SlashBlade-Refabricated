package cn.sh1rocu.slashblade.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.ApiStatus;

public abstract class InputEvent extends BaseEvent {
    public static final Event<ClickInput> CLICK_INPUT_CALLBACK = EventFactory.createArrayBacked(ClickInput.class, callbacks -> event -> {
        for (ClickInput callback : callbacks) {
            callback.onClickInput(event);
        }
    });

    public interface ClickInput {
        void onClickInput(InteractionKeyMappingTriggered event);
    }

    public static class InteractionKeyMappingTriggered extends InputEvent implements ICancellableEvent {
        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public void setSwingHand(boolean value) {
            handSwing = value;
        }

        public boolean shouldSwingHand() {
            return handSwing;
        }

        public InteractionHand getHand() {
            return hand;
        }

        public boolean isAttack() {
            return button == 0;
        }

        public boolean isUseItem() {
            return button == 1;
        }

        public boolean isPickBlock() {
            return button == 2;
        }

        public KeyMapping getKeyMapping() {
            return keyMapping;
        }
    }
}
