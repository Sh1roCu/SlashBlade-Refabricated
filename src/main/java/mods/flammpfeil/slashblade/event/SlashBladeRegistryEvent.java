package mods.flammpfeil.slashblade.event;

import cn.sh1rocu.slashblade.api.event.BaseEvent;
import cn.sh1rocu.slashblade.api.event.ICancellableEvent;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public abstract class SlashBladeRegistryEvent extends BaseEvent {
    private final SlashBladeDefinition definition;

    public static final Event<Pre.Callback> PRE = EventFactory.createArrayBacked(Pre.Callback.class, callbacks -> event -> {
        for (Pre.Callback callback : callbacks) {
            callback.onPre(event);
        }
    });
    public static final Event<Post.Callback> POST = EventFactory.createArrayBacked(Post.Callback.class, callbacks -> event -> {
        for (Post.Callback callback : callbacks) {
            callback.onPost(event);
        }
    });

    public SlashBladeRegistryEvent(SlashBladeDefinition definition) {
        this.definition = definition;
    }

    public SlashBladeDefinition getSlashBladeDefinition() {
        return definition;
    }

    public static class Pre extends SlashBladeRegistryEvent implements ICancellableEvent {
        public Pre(SlashBladeDefinition definition) {
            super(definition);
        }

        public interface Callback {
            void onPre(Pre event);
        }
    }

    public static class Post extends SlashBladeRegistryEvent {
        private final ItemStack blade;

        public Post(SlashBladeDefinition definition, ItemStack blade) {
            super(definition);
            this.blade = blade;
        }

        public ItemStack getBlade() {
            return blade;
        }

        public interface Callback {
            void onPost(Post event);
        }
    }
}
