package mods.flammpfeil.slashblade.compat.playerAnim;

import cn.sh1rocu.slashblade.util.LazyOptional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.Vec3f;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import jp.nyatla.nymmd.core.PmdBone;
import jp.nyatla.nymmd.types.MmdVector3;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VmdAnimation implements IAnimation {
    static final LazyOptional<MmdPmdModelMc> alex = LazyOptional.of(() -> {
        try {
            return new MmdPmdModelMc(Identifier.fromNamespaceAndPath(SlashBlade.MODID, "model/pa/alex.pmd"));
        } catch (MmdException | IOException e) {
            e.printStackTrace();
        }
        return null;
    });

    static final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
        MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();

        alex.ifPresent(pmd -> {
            try {
                mmp.setPmd(pmd);
            } catch (MmdException e) {
                e.printStackTrace();
            }
        });

        return mmp;
    });

    int currentTick;
    private int lastCachedTick = -1;
    private float lastCachedPartial = -1.0f;

    final Identifier loc;
    double start;
    double end;
    double span;
    boolean loop;

    private boolean isRunning = true;

    private boolean blendArms = false;
    private boolean blendLegs = true;

    static private Map<String, String> initNamemap() {
        Map<String, String> map = Maps.newHashMap();
        map.put("left_arm", "left arm");
        map.put("right_arm", "right arm");
        map.put("left_leg", "left leg");
        map.put("right_leg", "right leg");
        return map;
    }

    static final Map<String, String> nameMap = initNamemap();

    static final List<String> arms = Lists.newArrayList("left_arm", "right_arm");
    static final List<String> legs = Lists.newArrayList("left_leg", "right_leg");

    public VmdAnimation(Identifier loc, double start, double end, boolean loop) {
        this.loc = loc;
        this.start = start;
        this.end = end;

        this.span = TimeValueHelper.getTicksFromFrames((float) Math.abs(end - start));

        this.loop = loop;

        currentTick = 0;
    }

    public VmdAnimation getClone() {
        VmdAnimation tmp = new VmdAnimation(this.loc, this.start, this.end, this.loop);

        tmp.setBlendArms(this.blendArms);

        tmp.setBlendLegs(this.blendLegs);

        return tmp;
    }

    public VmdAnimation setBlendArms(boolean blend) {
        blendArms = blend;
        return this;
    }

    public VmdAnimation setBlendLegs(boolean blend) {
        blendLegs = blend;
        return this;
    }

    @Override
    public void tick(AnimationData data) {
        if (this.isRunning) {
            this.currentTick++;

            double endTicks = span;
            this.loop = false;
            if (this.loop && endTicks < this.currentTick) {
                this.currentTick = 0;
            }

            if (endTicks <= currentTick) {
                this.stop();
            }
        }
    }

    public void play() {
        this.currentTick = 0;
        this.isRunning = true;
    }

    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean isActive() {
        return this.isRunning;
    }

    @Override
    public void get3DTransform(@NonNull PlayerAnimBone value0) {
        this.setupAnim(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));

        String modelName = value0.name;

        double motionScale = 1.0 / 16.0;
        float bodyScale = (float) motionScale;
        float finalizeScale = 2.0f;

        if (((!this.blendArms && arms.contains(modelName)) || (!this.blendLegs && legs.contains(modelName)))) {
            value0.rotation.mul(0);
        }

        if (!motionPlayer.isPresent())
            return;
        MmdMotionPlayerGL2 mmp = motionPlayer.orElse(null);

        String boneName = modelName;
        if (nameMap.containsKey(modelName)) {
            boneName = nameMap.get(modelName);
        }

        PmdBone bone = mmp.getBoneByName(boneName);

        if (bone != null) {
            {
                MmdVector3 org = bone.m_vec3Position;
                Vector3f tmp = new Vector3f(org.x, org.y, org.z);
                if (modelName.equals("body")) {
                    tmp = tmp.mul(bodyScale);
                } else {
                    tmp = tmp.mul(1, -1, 1);
                }

                tmp.mul(finalizeScale).add(value0.position);
                value0.position.set(tmp);
            }

            {
                Quaterniond qt = new Quaterniond(bone.m_vec4Rotate.x, bone.m_vec4Rotate.y, bone.m_vec4Rotate.z,
                        bone.m_vec4Rotate.w);
                Vector3d tmp = QuaternionToEulerZYX(qt);

                if (modelName.equals("body")) {
                    tmp = tmp.mul(1, -1, -1);
                } else {
                    tmp = tmp.mul(-1, 1, -1);
                }

                tmp.add(value0.rotation);
                value0.rotation.set(tmp);
            }
        }
        /**/

        /*
         * int idx = mmp.getBoneIndexByName(boneName); if (0 <= idx) { float[] buf = new
         * float[16]; mmp._skinning_mat[idx].getValue(buf);
         *
         * Matrix4f mat = VectorHelper.matrix4fFromArray(buf); mat = (new
         * Matrix4f()).scale(1, -1, 1).mul(mat).scale(1,-1,1).scale((float)scale);
         * //mat.transpose();
         *
         * switch (type){ case POSITION -> { Vector3f tmp = new Vector3f();
         * mat.getTranslation(tmp); MmdVector3 vec = bone._pmd_bone_position;
         *
         * return new Vec3f(tmp.x,tmp.y,tmp.z).add(value0); } case ROTATION -> {
         *
         * Quaternionf qt = new
         * Quaternionf(bone.m_vec4Rotate.x,bone.m_vec4Rotate.y,bone.m_vec4Rotate.z,bone.
         * m_vec4Rotate.w); Vector3f tmp = new Vector3f(); qt.getEulerAnglesXYZ(tmp);
         *
         * return new Vec3f(tmp.x,tmp.y,tmp.z);
         *
         * //Vector3f tmp = new Vector3f(); //mat = mat; //mat.getEulerAnglesZYX(tmp);
         * //return new Vec3f(tmp.x,tmp.y,tmp.z);
         *
         * } } } /
         **/

    }

    Vector3d QuaternionToEulerZYX(Quaterniond qt) {
        Vector3d tmp = new Vector3d();

        // 1. 归一化四元数
        Quaterniond normalizedQt = qt.normalize();

        // 2. 计算旋转矩阵元素(修正后的公式)
        double wx = normalizedQt.w * normalizedQt.x;
        double wy = normalizedQt.w * normalizedQt.y;
        double wz = normalizedQt.w * normalizedQt.z;
        double xx = normalizedQt.x * normalizedQt.x;
        double xy = normalizedQt.x * normalizedQt.y;
        double xz = normalizedQt.x * normalizedQt.z;
        double yy = normalizedQt.y * normalizedQt.y;
        double yz = normalizedQt.y * normalizedQt.z;
        double zz = normalizedQt.z * normalizedQt.z;

        // 旋转矩阵 R 的元素(ZYX顺序)
        double m00 = 1.0 - 2.0 * (yy + zz);
        double m01 = 2.0 * (xy + wz);
        double m02 = 2.0 * (xz - wy);
        double m12 = 2.0 * (yz + wx);
        double m22 = 1.0 - 2.0 * (xx + yy);

        // 3. 计算欧拉角(Z-Y-X顺序)
        tmp.z = Math.atan2(m01, m00);
        tmp.y = Math.asin(-m02);  // 确保参数在-1~1内
        tmp.x = Math.atan2(m12, m22);

        return tmp;
        
/*        Vector3d tmp = new Vector3d();

        double a_x_x = Math.pow(qt.w, 2) + Math.pow(qt.x, 2) - Math.pow(qt.y, 2) - Math.pow(qt.z, 2);
        double a_x_y = 2 * (qt.x * qt.y + qt.w * qt.z);
        double a_x_z = 2 * (qt.x * qt.z - qt.w * qt.y);

//        double a_y_x = 2 * (qt.x * qt.y - qt.w * qt.z);
//        double a_y_y = Math.pow(qt.w, 2) - Math.pow(qt.x, 2) + Math.pow(qt.y, 2) - Math.pow(qt.z, 2);
        double a_y_z = 2 * (qt.y * qt.z + qt.w * qt.x);

//        double a_z_x = 2 * (qt.x * qt.z + qt.w * qt.y);
//        double a_z_y = 2 * (qt.y * qt.z - qt.w * qt.x);
        double a_z_z = Math.pow(qt.w, 2) - Math.pow(qt.x, 2) - Math.pow(qt.y, 2) + Math.pow(qt.z, 2);

        // Quaternion to Euler zyx
        tmp.z = Math.atan2(a_x_y, a_x_x);
        tmp.y = Math.asin(-a_x_z);
        tmp.x = Math.atan2(a_y_z, a_z_z);

        return tmp;*/
    }

    @Override
    public void setupAnim(AnimationData data) {
        this.setupAnim(data.getPartialTick());
    }

    public void setupAnim(float tickDelta) {
        if (!motionPlayer.isPresent())
            return;

        if (this.currentTick == this.lastCachedTick
                && Float.floatToIntBits(tickDelta) == Float.floatToIntBits(this.lastCachedPartial)) {
            return;
        }
        this.lastCachedTick = this.currentTick;
        this.lastCachedPartial = tickDelta;

        MmdMotionPlayerGL2 mmp = motionPlayer.orElse(null);

        double eofTime = 0;
        MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(loc);
        if (motion != null) {
            try {
                mmp.setVmd(motion);
                eofTime = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
            } catch (Exception e) {
                SlashBlade.LOGGER.warn(e);
            }
        } else if (!mmp.hasVmdMotion()) {
            return;
        }

        double time = TimeValueHelper.getMSecFromTicks((float) (currentTick + (double) tickDelta));
        time = Math.min(eofTime, time);
        time = TimeValueHelper.getMSecFromFrames((float) start) + time;

        try {
            mmp.updateMotionBonesOnly((float) time);
        } catch (MmdException e) {
            SlashBlade.LOGGER.warn(e);
        }
    }
}
