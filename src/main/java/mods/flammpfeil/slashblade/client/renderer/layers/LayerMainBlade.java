package mods.flammpfeil.slashblade.client.renderer.layers;

import cn.sh1rocu.slashblade.api.RenderStateKeys;
import cn.sh1rocu.slashblade.util.LazyOptional;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.special.BladeBaseRenderer;
import mods.flammpfeil.slashblade.client.renderer.special.state.BladeItemRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.data.tag.SlashBladeEntityTypeTagProvider;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.IOException;
import java.util.Optional;

public class LayerMainBlade<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public LayerMainBlade(RenderLayerParent<S, M> entityRendererIn) {
        super(entityRendererIn);
    }

    final LazyOptional<MmdPmdModelMc> bladeholder = LazyOptional.of(() -> {
        try {
            return new MmdPmdModelMc(Identifier.fromNamespaceAndPath(SlashBlade.MODID, "model/bladeholder.pmd"));
        } catch (MmdException | IOException e) {
            e.printStackTrace();
        }
        return null;
    });

    final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
        MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();

        bladeholder.ifPresent(pmd -> {
            try {
                mmp.setPmd(pmd);
            } catch (MmdException e) {
                e.printStackTrace();
            }
        });

        return mmp;
    });

    private static final Quaternionf CARRY_ROTATION_PSO2 =
            new Quaternionf().rotateZYX(-0.122173F, 0, 0);
    private static final Quaternionf CARRY_ROTATION_KATANA =
            new Quaternionf().rotateZYX(3.1415927F, 1.570796f, 0.261799F);
    private static final Quaternionf CARRY_ROTATION_DEFAULT =
            new Quaternionf().rotateZYX(0F, 1.570796f, 0.261799F);
    private static final Quaternionf CARRY_ROTATION_NINJA =
            new Quaternionf().rotateZYX(-2.094395F, 0f, 3.1415927F);
    private static final Quaternionf CARRY_ROTATION_RNINJA =
            new Quaternionf().rotateZYX(-1.047198F, 0, 0);

    private static final String BLADE_LUMINOUS = "blade_luminous";
    private static final String BLADE_DAMAGED_LUMINOUS = "blade_damaged_luminous";
    private static final String SHEATH_LUMINOUS = "sheath_luminous";

    private final float[] boneMatrixBuf = new float[16];
    private final Matrix3f normalMatrixTmp = new Matrix3f();

    public float modifiedSpeed(float baseSpeed, LivingEntity entity) {
        float modif = 6.0f;
        if (MobEffectUtil.hasDigSpeed(entity)) {
            modif = 6 - (1 + MobEffectUtil.getDigSpeedAmplification(entity));
        } else if (entity.hasEffect(MobEffects.MINING_FATIGUE)) {
            modif = 6 + (1 + entity.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2;
        }

        modif /= 6.0f;

        return baseSpeed / modif;
    }

    public void renderOffhandItem(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn, S renderState) {
        if (!(renderState instanceof ArmedEntityRenderState armedEntityRenderState)) {
            return;
        }

        ItemStack offhandStack = armedEntityRenderState.mainArm == HumanoidArm.RIGHT ?
                armedEntityRenderState.leftHandItemStack : armedEntityRenderState.rightHandItemStack;
        if (offhandStack.isEmpty() || CapabilitySlashBlade.getBladeState(offhandStack).isEmpty()) {
            renderHotbarItem(matrixStack, submitNodeCollector, lightIn, armedEntityRenderState);
            return;
        }

        renderStandbyBlade(matrixStack, submitNodeCollector, lightIn, offhandStack, armedEntityRenderState);
    }

    public void renderHotbarItem(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn, ArmedEntityRenderState renderState) {
        if (renderState instanceof AvatarRenderState player) {
            if (player.getDataOrDefault(RenderStateKeys.SELECTED_SLOT, 0) == 0)
                return;

            ItemStack blade = player.getDataOrDefault(RenderStateKeys.FIRST_INV_ITEM, ItemStack.EMPTY);
            if (blade.isEmpty())
                return;

            renderStandbyBlade(matrixStack, submitNodeCollector, lightIn, blade, player);
        }
    }

    public void renderStandbyBlade(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn, ItemStack blade, ArmedEntityRenderState entity) {
        Optional<ISlashBladeState> state = CapabilitySlashBlade.getBladeState(blade);
        int entityId = entity.getDataOrDefault(RenderStateKeys.ENTITY_ID, -99);
        state.ifPresent(s -> {
            double modelScaleBase = 0.0078125F; // 0.5^7
            double motionScale = 1.5 / 12.0;
            Identifier textureLocation = s.getTexture().orElse(DefaultResources.resourceDefaultTexture);

            WavefrontObject obj = BladeModelManager.getInstance()
                    .getModel(s.getModel().orElse(DefaultResources.resourceDefaultModel));
            String part;
            try (MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)) {
                // minecraft model neckPoint height = 1.5f
                // mmd model neckPoint height = 12.0f
                matrixStack.translate(0, 1.5f, 0);
                var carrytype = s.getCarryType();
                final Minecraft mcinstance = Minecraft.getInstance();
                switch (carrytype) {
                    case PSO2:
                        matrixStack.translate(1F, -1.125f, 0.20f);
                        matrixStack.mulPose(CARRY_ROTATION_PSO2);
                        if (mcinstance.options.getCameraType() == CameraType.FIRST_PERSON
                                && entityId == mcinstance.player.getId()) return;
                        break;

                    case KATANA:
                        matrixStack.translate(0.25F, -0.875f, -0.55f);
                        matrixStack.mulPose(CARRY_ROTATION_KATANA);
                        break;

                    case DEFAULT:
                        matrixStack.translate(0.25F, -0.875f, -0.55f);
                        matrixStack.mulPose(CARRY_ROTATION_DEFAULT);
                        break;

                    case NINJA:
                        matrixStack.translate(-0.5F, -2f, 0.20f);
                        matrixStack.mulPose(CARRY_ROTATION_NINJA);
                        if (mcinstance.options.getCameraType() == CameraType.FIRST_PERSON
                                && entityId == mcinstance.player.getId()) return;
                        break;

                    case RNINJA:
                        matrixStack.translate(0.5F, -2f, 0.20f);
                        matrixStack.mulPose(CARRY_ROTATION_RNINJA);
                        if (mcinstance.options.getCameraType() == CameraType.FIRST_PERSON
                                && entityId == mcinstance.player.getId()) return;
                        break;

                    default:
                        return;
                }

                float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);
                matrixStack.scale(modelScale, modelScale, modelScale);

                try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                    if (s.isBroken()) {
                        part = "blade_damaged";
                    } else {
                        part = "blade";
                    }

                    BladeItemRenderState renderState = BladeBaseRenderer.createBladeItemRenderState(blade);

                    BladeRenderState.renderOverrided(renderState, obj, part, textureLocation, matrixStack, submitNodeCollector,
                            lightIn);
                    BladeRenderState.renderOverridedLuminous(renderState, obj,
                            s.isBroken() ? BLADE_DAMAGED_LUMINOUS : BLADE_LUMINOUS, textureLocation,
                            matrixStack, submitNodeCollector, lightIn);
                    BladeRenderState.renderOverrided(renderState, obj, "sheath", textureLocation, matrixStack, submitNodeCollector,
                            lightIn);
                    BladeRenderState.renderOverridedLuminous(renderState, obj, SHEATH_LUMINOUS, textureLocation,
                            matrixStack, submitNodeCollector, lightIn);
                }
            }
        });
    }

    @Override
    public void submit(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int lightIn, S state, float yRot, float xRot) {
        if (!(state instanceof ArmedEntityRenderState entity)) {
            return;
        }

        this.renderOffhandItem(matrixStack, submitNodeCollector, lightIn, state);

        float motionYOffset = 1.5f;
        double motionScale = 1.5 / 12.0;
        double modelScaleBase = 0.0078125F; // 0.5^7

        ItemStack stack = entity.getMainHandItemStack();

        if (stack.isEmpty())
            return;

        if (entity.entityType.builtInRegistryHolder().is(SlashBladeEntityTypeTagProvider.EntityTypeTags.RENDER_LAYER_BLACKLIST))
            return;

        CapabilitySlashBlade.getBladeState(stack).ifPresent(s -> {
            motionPlayer.ifPresent(mmp -> {
                double time = entity.getDataOrDefault(RenderStateKeys.COMBO_STATE_TIME, 0D);
                ComboState combo = entity.getDataOrDefault(RenderStateKeys.COMBO_STATE, ComboStateRegistry.NONE);
                MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());

                double maxSeconds = 0;
                if (motion != null) {
                    try {
                        mmp.setVmd(motion);
                        maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                double start = TimeValueHelper.getMSecFromFrames(combo.getStartFrame());
                double end = TimeValueHelper.getMSecFromFrames(combo.getEndFrame());
                double span = Math.abs(end - start);

                span = Math.min(maxSeconds, span);

                if (combo.getLoop()) {
                    time = time % span;
                }
                time = Math.min(span, time);

                time = start + time;

                try {
                    mmp.updateMotionBonesAndSkinning((float) time);
                } catch (MmdException e) {
                    e.printStackTrace();
                }

                try (MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)) {

                    setUserPose(matrixStack, entity, entity.sb$partialTick());

                    // minecraft model neckPoint height = 1.5f
                    // mmd model neckPoint height = 12.0f
                    matrixStack.translate(0, motionYOffset, 0);

                    matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);

                    // transpoze mmd to mc
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180));

                    Identifier textureLocation = s.getTexture().orElse(DefaultResources.resourceDefaultTexture);

                    WavefrontObject obj = BladeModelManager.getInstance()
                            .getModel(s.getModel().orElse(DefaultResources.resourceDefaultModel));

                    BladeItemRenderState renderState = BladeBaseRenderer.createBladeItemRenderState(stack);

                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointA");

                        if (0 <= idx) {
                            mmp._skinning_mat[idx].getValue(boneMatrixBuf);

                            Matrix4f mat = VectorHelper.matrix4fFromArray(boneMatrixBuf);

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            entry.normal().mul(normalMatrixTmp.set(mat).invert().transpose());
                            matrixStack.scale(-1, 1, 1);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);

                        String part;
                        if (s.isBroken()) {
                            part = "blade_damaged";
                        } else {
                            part = "blade";
                        }
                        BladeRenderState.renderOverrided(renderState, obj, part, textureLocation, matrixStack, submitNodeCollector,
                                lightIn);
                        BladeRenderState.renderOverridedLuminous(renderState, obj,
                                s.isBroken() ? BLADE_DAMAGED_LUMINOUS : BLADE_LUMINOUS, textureLocation,
                                matrixStack, submitNodeCollector, lightIn);
                    }

                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointB");

                        if (0 <= idx) {
                            mmp._skinning_mat[idx].getValue(boneMatrixBuf);

                            Matrix4f mat = VectorHelper.matrix4fFromArray(boneMatrixBuf);

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            entry.normal().mul(normalMatrixTmp.set(mat).invert().transpose());
                            matrixStack.scale(-1, 1, 1);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);
                        BladeRenderState.renderOverrided(renderState, obj, "sheath", textureLocation, matrixStack, submitNodeCollector,
                                lightIn);
                        BladeRenderState.renderOverridedLuminous(renderState, obj, SHEATH_LUMINOUS, textureLocation,
                                matrixStack, submitNodeCollector, lightIn);

                        if (entity.getDataOrDefault(RenderStateKeys.BLADE_IS_CHARGED, false)) {
                            float f = (float) entity.getDataOrDefault(RenderStateKeys.TICK_COUNT, 0) + entity.sb$partialTick();
                            BladeRenderState.renderChargeEffect(renderState, f, obj, "effect",
                                    Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png"), matrixStack,
                                    submitNodeCollector, lightIn);
                        }

                    }

                }
            });
        });
    }

    public void setUserPose(PoseStack matrixStack, LivingEntityRenderState renderState, float partialTicks) {
        if (!UserPoseOverrider.UsePoseOverrider && renderState instanceof IAvatarAnimationState avatarRenderState) {
            var animationPlayer = avatarRenderState.playerAnimLib$getAnimManager();
            animationPlayer.setTickDelta(partialTicks);
            if (animationPlayer.isActive()) {
                PlayerAnimBone bone = animationPlayer.get3DTransform("body");
                matrixStack.translate(-bone.position.x(), (bone.position.y() + 0.7), -bone.position.z());
                // These are additive properties
                matrixStack.mulPose(Axis.ZP.rotation(bone.rotation.z())); // roll
                matrixStack.mulPose(Axis.YP.rotation(bone.rotation.y())); // pitch
                matrixStack.mulPose(Axis.XP.rotation(bone.rotation.x())); // yaw
                matrixStack.translate(0, -0.7d, 0);
            }
        } else {
            UserPoseOverrider.invertRot(matrixStack, renderState, partialTicks);
        }
    }
}
