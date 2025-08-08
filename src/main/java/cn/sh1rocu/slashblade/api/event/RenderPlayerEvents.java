package cn.sh1rocu.slashblade.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;

// From Porting_Lib
public class RenderPlayerEvents {
    public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (player, renderer, partialTick, poseStack, buffer, packedLight) -> {
        for (Pre callback : callbacks)
            if (callback.onPreRenderPlayer(player, renderer, partialTick, poseStack, buffer, packedLight))
                return true;
        return false;
    });

    public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (player, renderer, partialTick, poseStack, buffer, packedLight) -> {
        for (Post callback : callbacks)
            callback.onPostRenderPlayer(player, renderer, partialTick, poseStack, buffer, packedLight);
    });


    public interface Pre {
        boolean onPreRenderPlayer(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight);
    }

    public interface Post {
        void onPostRenderPlayer(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight);
    }
}