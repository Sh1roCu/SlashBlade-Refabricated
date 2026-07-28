package mods.flammpfeil.slashblade.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBladeDetune;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlashBladeTEISR {
    private SlashBladeTEISR() {

    }

    public static void renderIcon(BladeItemRenderState state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn,
                                  float scale) {
        renderIcon(state, matrixStack, submitNodeCollector, lightIn, scale, false);
    }

    public static void renderIcon(BladeItemRenderState state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn,
                                  float scale, boolean renderDurability) {

        matrixStack.scale(scale, scale, scale);

        EnumSet<SwordType> types = state.swordTypes;

        Identifier modelLocation = state.modelLocation;
        WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);
        Identifier textureLocation = state.textureLocation;

        String renderTarget;
        if (types.contains(SwordType.BROKEN)) {
            renderTarget = "item_damaged";
        } else if (types.contains(SwordType.NOSCABBARD)) {
            renderTarget = "item_bladens";
        } else {
            renderTarget = "item_blade";
        }

        BladeRenderState.renderOverrided(state, model, renderTarget, textureLocation, matrixStack, submitNodeCollector, lightIn);
        BladeRenderState.renderOverridedLuminous(state, model, renderTarget + "_luminous", textureLocation, matrixStack,
                submitNodeCollector, lightIn);

        if (renderDurability) {

            WavefrontObject durabilityModel = BladeModelManager.getInstance()
                    .getModel(DefaultResources.resourceDurabilityModel);

            float durability = (float) state.damageValue / (float) state.maxDamage;
            matrixStack.translate(0.0F, 0.0F, 0.1f);

            Color aCol = new Color(0.25f, 0.25f, 0.25f, 1.0f);
            Color bCol = new Color(0xA52C63);
            int r = 0xFF & (int) Mth.lerp(aCol.getRed(), bCol.getRed(), durability);
            int g = 0xFF & (int) Mth.lerp(aCol.getGreen(), bCol.getGreen(), durability);
            int b = 0xFF & (int) Mth.lerp(aCol.getBlue(), bCol.getBlue(), durability);

            BladeRenderState.setCol(new Color(r, g, b));
            BladeRenderState.renderOverrided(state, durabilityModel, "base", DefaultResources.resourceDurabilityTexture,
                    matrixStack, submitNodeCollector, lightIn);

            boolean isBroken = types.contains(SwordType.BROKEN);
            matrixStack.translate(0.0F, 0.0F, -2.0f * durability);

            BladeRenderState.renderOverrided(state, durabilityModel, isBroken ? "color_r" : "color",
                    DefaultResources.resourceDurabilityTexture, matrixStack, submitNodeCollector, lightIn);

        }
    }

    public static Identifier stackDefaultModel(ItemStack stack) {
        var cap = CapabilitySlashBlade.getBladeState(stack);
        if (cap.isEmpty())
            return DefaultResources.resourceDefaultModel;
        String name = cap.get().getModel().map(Identifier::toString).orElse("");
        if (!(stack.getItem() instanceof ItemSlashBladeDetune)) {
            String key = cap.get().getTranslationKey();
            if (!key.isBlank()) {
                Identifier bladeName = Identifier.tryParse(key.substring(5)
                        .replaceFirst(Pattern.quote("."), Matcher.quoteReplacement(":"))
                        // 附属包会存在namespace:path1/path2这种格式的ResourceLocation，需要将'.'替换为'/'
                        .replace(".", "/"));
                SlashBladeDefinition slashBladeDefinition = BladeModelManager.getClientSlashBladeRegistry().getValue(bladeName);

                if (slashBladeDefinition != null)
                    name = slashBladeDefinition.getRenderDefinition().getModelName().toString();
            }
        }
        return !name.isBlank()
                ? Identifier.tryParse(name) : DefaultResources.resourceDefaultModel;
    }

    public static Identifier stackDefaultTexture(ItemStack stack) {
        var cap = CapabilitySlashBlade.getBladeState(stack);
        if (cap.isEmpty())
            return DefaultResources.resourceDefaultTexture;
        String name = cap.get().getTexture().map(Identifier::toString).orElse("");
        if (!(stack.getItem() instanceof ItemSlashBladeDetune)) {
            String key = cap.get().getTranslationKey();
            if (!key.isBlank()) {
                Identifier bladeName = Identifier.tryParse(key.substring(5)
                        .replaceFirst(Pattern.quote("."), Matcher.quoteReplacement(":"))
                        // 附属包会存在namespace:path1/path2这种格式的ResourceLocation，需要将'.'替换为'/'
                        .replace(".", "/"));
                SlashBladeDefinition slashBladeDefinition = BladeModelManager.getClientSlashBladeRegistry().getValue(bladeName);
                if (slashBladeDefinition != null)
                    name = slashBladeDefinition.getRenderDefinition().getTextureName().toString();
            }
        }
        return !name.isBlank()
                ? Identifier.tryParse(name) : DefaultResources.resourceDefaultTexture;
    }

    public static void renderModel(BladeItemRenderState state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn) {

        float scale = 0.003125f;
        matrixStack.scale(scale, scale, scale);
        float defaultOffset = 130;
        matrixStack.translate(defaultOffset, 0, 0);

        EnumSet<SwordType> types = state.swordTypes;
        // BladeModel.itemBlade.getModelLocation(itemStackIn)

        Identifier modelLocation = state.modelLocation;
        WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);
        Identifier textureLocation = state.textureLocation;

        Vec3 bladeOffset = Vec3.ZERO;
        float bladeOffsetRot = 0;
        float bladeOffsetBaseRot = -3;
        Vec3 sheathOffset = Vec3.ZERO;
        float sheathOffsetRot = 0;
        float sheathOffsetBaseRot = -3;
        boolean vFlip = false;
        boolean hFlip = false;
        boolean hasScabbard = true;

        if (state.isFramed && state.bladeStandState != null) {
            Item type = state.bladeStandState.currentType;
            Pose pose = state.bladeStandState.pose;
            switch (pose.ordinal()) {
                case 0:
                    vFlip = false;
                    hFlip = false;
                    break;
                case 1:
                    vFlip = true;
                    hFlip = false;
                    break;
                case 2:
                    vFlip = true;
                    hFlip = true;
                    break;
                case 3:
                    vFlip = false;
                    hFlip = true;
                    break;
                case 4:
                    vFlip = false;
                    hFlip = false;
                    hasScabbard = false;
                    break;
                case 5:
                    vFlip = false;
                    hFlip = true;
                    hasScabbard = false;
                    break;
            }

            if (type == SBItems.BLADESTAND_1) {
                bladeOffset = Vec3.ZERO;
                sheathOffset = Vec3.ZERO;
            } else if (type == SBItems.BLADESTAND_2) {
                bladeOffset = new Vec3(0, 21.5f, 0);
                if (hFlip) {
                    sheathOffset = new Vec3(-40, -27, 0);
                } else {
                    sheathOffset = new Vec3(40, -27, 0);
                }
                sheathOffsetBaseRot = -4;
            } else if (type == SBItems.BLADESTAND_V) {
                bladeOffset = new Vec3(-100, 230, 0);
                sheathOffset = new Vec3(-100, 230, 0);
                bladeOffsetRot = 80;
                sheathOffsetRot = 80;
            } else if (type == SBItems.BLADESTAND_S) {
                if (hFlip) {
                    bladeOffset = new Vec3(60, -25, 0);
                    sheathOffset = new Vec3(60, -25, 0);
                } else {
                    bladeOffset = new Vec3(-60, -25, 0);
                    sheathOffset = new Vec3(-60, -25, 0);
                }
            } else if (type == SBItems.BLADESTAND_1_W) {
                bladeOffset = Vec3.ZERO;
                sheathOffset = Vec3.ZERO;
            } else if (type == SBItems.BLADESTAND_2_W) {
                bladeOffset = new Vec3(0, 21.5f, 0);
                if (hFlip) {
                    sheathOffset = new Vec3(-40, -27, 0);
                } else {
                    sheathOffset = new Vec3(40, -27, 0);
                }
                sheathOffsetBaseRot = -4;
            }
        }

        try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
            String renderTarget;
            if (types.contains(SwordType.BROKEN))
                renderTarget = "blade_damaged";
            else
                renderTarget = "blade";

            matrixStack.translate(bladeOffset.x, bladeOffset.y, bladeOffset.z);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(bladeOffsetRot));

            if (vFlip) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(180.0f));
                matrixStack.translate(0, -15, 0);

                matrixStack.translate(0, 5, 0);
            }

            if (hFlip) {
                double offset = defaultOffset;
                matrixStack.translate(-offset, 0, 0);
                matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                matrixStack.translate(offset, 0, 0);
            }

            matrixStack.mulPose(Axis.ZP.rotationDegrees(bladeOffsetBaseRot));

            BladeRenderState.renderOverrided(state, model, renderTarget, textureLocation, matrixStack, submitNodeCollector,
                    lightIn);
            BladeRenderState.renderOverridedLuminous(state, model, renderTarget + "_luminous", textureLocation,
                    matrixStack, submitNodeCollector, lightIn);
        }

        if (hasScabbard) {
            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                String renderTarget = "sheath";

                matrixStack.translate(sheathOffset.x, sheathOffset.y, sheathOffset.z);
                matrixStack.mulPose(Axis.ZP.rotationDegrees(sheathOffsetRot));

                if (vFlip) {
                    matrixStack.mulPose(Axis.XP.rotationDegrees(180.0f));
                    matrixStack.translate(0, -15, 0);

                    matrixStack.translate(0, 5, 0);
                }

                if (hFlip) {
                    double offset = defaultOffset;
                    matrixStack.translate(-offset, 0, 0);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                    matrixStack.translate(offset, 0, 0);
                }

                matrixStack.mulPose(Axis.ZP.rotationDegrees(sheathOffsetBaseRot));

                BladeRenderState.renderOverrided(state, model, renderTarget, textureLocation, matrixStack, submitNodeCollector,
                        lightIn);
                BladeRenderState.renderOverridedLuminous(state, model, renderTarget + "_luminous", textureLocation,
                        matrixStack, submitNodeCollector, lightIn);
            }
        }

    }
}
