package mods.flammpfeil.slashblade.client.renderer.gui;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class RankRenderer implements HudElement {
    private static final class SingletonHolder {
        private static final RankRenderer instance = new RankRenderer();
    }

    public static RankRenderer getInstance() {
        return SingletonHolder.instance;
    }

    private RankRenderer() {
    }

    public void register() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, SlashBlade.prefix("rank"), RankRenderer.getInstance());
    }

    static Identifier RankImg = Identifier.fromNamespaceAndPath(SlashBlade.MODID, "textures/gui/rank.png");

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker timer) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        // if(!mc.isGameFocused()) return;
        if (!Minecraft.renderNames())
            return;
        if (mc.screen != null) {
            if (!(mc.screen instanceof ChatScreen))
                return;
        }

        LocalPlayer player = mc.player;
        long time = System.currentTimeMillis();

        renderRankHud(graphics, timer, player, time);
    }

    private void renderRankHud(GuiGraphicsExtractor graphics, DeltaTracker timer, LocalPlayer player, long time) {
        Minecraft mc = Minecraft.getInstance();

        CapabilityConcentrationRank.RANK_POINT.maybeGet(player).ifPresent(cr -> {
            long now = player.level().getGameTime();

            IConcentrationRank.ConcentrationRanks rank = cr.getRank(now);

            /*
             * debug rank = IConcentrationRank.ConcentrationRanks.C; now =
             * cr.getLastUpdate();
             */

            if (rank == IConcentrationRank.ConcentrationRanks.NONE)
                return;

            // todo : korenani loadGUIRenderMatrix
            // mc.getMainWindow().loadGUIRenderMatrix(Minecraft.IS_RUNNING_ON_MAC);

            int k = mc.getWindow().getGuiScaledWidth();
            int l = mc.getWindow().getGuiScaledHeight();

            // position
            int baseX = k * 2 / 3;
            int baseY = l / 5;

            boolean showTextRank = false;

            long textTimeout = cr.getLastRankRise() + 20;
            long visibleTimeout = cr.getLastUpdate() + 120;

            if (now < textTimeout)
                showTextRank = true;

            if (now < visibleTimeout) {
                int rankOffset = 32 * (rank.level - 1);
                int textOffset = showTextRank ? 128 : 0;

                int progress = (int) (33 * cr.getRankProgress(now));

                int progressIcon = (int) (18 * cr.getRankProgress(now));
                int progressIconInv = 17 - progressIcon;

                // iconFrame
                blit(graphics, baseX, baseY, textOffset + 64, rankOffset, 64, 32);
                // icon
                blit(graphics, baseX, baseY + progressIconInv + 7, textOffset,
                        rankOffset + progressIconInv + 7, 64, progressIcon);
                // gauge frame
                blit(graphics, baseX, baseY + 32, 0, 256 - 16, 64, 16);
                // gauge fill
                blit(graphics, baseX + 16, baseY + 32, 16, 256 - 32, progress, 16);
            }

        });

    }

    private static void blit(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                RankImg,
                x, y,
                u, v,
                width, height,
                width, height,
                256, 256
        );
    }
}
