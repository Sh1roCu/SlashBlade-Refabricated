package mods.flammpfeil.slashblade.client.renderer;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import cn.sh1rocu.slashblade.api.event.RenderLivingEvent;
import cn.sh1rocu.slashblade.mixin.accessor.EntityRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Optional;

public class LockonCircleRender {
    private static final class SingletonHolder {
        private static final LockonCircleRender instance = new LockonCircleRender();
    }

    public static LockonCircleRender getInstance() {
        return SingletonHolder.instance;
    }

    private LockonCircleRender() {
    }

    public void register() {
        RenderLivingEvent.PRE.register(this::onRenderLiving);
        RenderLivingEvent.POST.register(this::onRenderLiving);
    }

    static final Identifier modelLoc = Identifier.fromNamespaceAndPath("slashblade", "model/util/lockon.obj");
    static final Identifier textureLoc = Identifier.fromNamespaceAndPath("slashblade", "model/util/lockon.png");

    @SuppressWarnings("rawtypes")
    public void onRenderLiving(RenderLivingEvent event) {
        LivingEntityRenderState livingEntity = event.getRenderState();
        var renderer = event.getRenderer();
        PoseStack poseStack = event.getPoseStack();
        float partialTicks = event.getPartialTick();
        var submitNodeCollector = event.getSubmitNodeCollector();
        final Minecraft minecraftInstance = Minecraft.getInstance();
        Player player = minecraftInstance.player;
        if (player == null)
            return;
        if (CapabilityInputState.INPUT_STATE.maybeGet(player)
                .filter(input -> input.getCommands().contains(InputCommand.SNEAK)).isEmpty())
            return;

        ItemStack stack = player.getMainHandItem();
        Level level = player.level();
        Optional<Color> effectColor = CapabilitySlashBlade.getBladeState(stack)
                .filter(s -> livingEntity.getDataOrDefault(RenderStateKeys.ENTITY_ID, -99) == s.getTargetEntityId()).map(ISlashBladeState::getEffectColor);

        if (effectColor.isEmpty())
            return;

        if (!livingEntity.getDataOrDefault(RenderStateKeys.IS_ALIVE, false))
            return;

        float health = livingEntity.getDataOrDefault(RenderStateKeys.HP, 0F)
                / livingEntity.getDataOrDefault(RenderStateKeys.MAX_HP, 0F);

        Color col = new Color(effectColor.get().getRGB() & 0xFFFFFF | 0xAA000000, true);

        float f = livingEntity.getDataOrDefault(RenderStateKeys.BB_HEIGHT, 0F) * 0.5f;

        poseStack.pushPose();
        poseStack.translate(0.0D, f, 0.0D);

        Vec3 offset = ((EntityRendererAccessor) renderer).sb$getEntityRenderDispatcher().camera.position()
                .subtract(livingEntity.getDataOrDefault(RenderStateKeys.POSITION, Vec3.ZERO).add(0, f, 0));
        offset = offset.scale(0.5f);
        poseStack.translate(offset.x(), offset.y(), offset.z());

        poseStack.mulPose(((EntityRendererAccessor) renderer).sb$getEntityRenderDispatcher().camera.rotation());
        // poseStack.scale(-0.025F, -0.025F, 0.025F);

        float scale = 0.0025f;
        poseStack.scale(scale, -scale, scale);

        WavefrontObject model = BladeModelManager.getInstance().getModel(modelLoc);
        Identifier resourceTexture = textureLoc;

        final String base = "lockonBase";
        final String mask = "lockonHealthMask";
        final String value = "lockonHealth";

        BladeRenderState.setCol(col);
        BladeRenderState.renderOverridedLuminous(BladeItemRenderState.EMPTY, model, base, resourceTexture, poseStack, submitNodeCollector,
                BladeRenderState.MAX_LIGHT);
        {
            poseStack.pushPose();
            poseStack.translate(0, 0, health * 10.0f);
            BladeRenderState.setCol(new Color(0x20000000, true));
            BladeRenderState.renderOverridedLuminousDepthWrite(BladeItemRenderState.EMPTY, model, mask, resourceTexture, poseStack,
                    submitNodeCollector, BladeRenderState.MAX_LIGHT);
            poseStack.popPose();
        }
        BladeRenderState.setCol(col);
        BladeRenderState.renderOverridedLuminousDepthWrite(BladeItemRenderState.EMPTY, model, value, resourceTexture, poseStack,
                submitNodeCollector, BladeRenderState.MAX_LIGHT);

        poseStack.popPose();
    }
}
