package mods.flammpfeil.slashblade.compat.playerAnim;

import com.google.common.collect.Maps;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class PlayerAnimationOverrider {
    private Map<ResourceLocation, VmdAnimation> animation = initAnimations();

    private static final class SingletonHolder {
        private static final PlayerAnimationOverrider instance = new PlayerAnimationOverrider();
    }

    public static PlayerAnimationOverrider getInstance() {
        return SingletonHolder.instance;
    }

    private PlayerAnimationOverrider() {
    }

    public void register() {
        BladeMotionEvent.CALLBACK.register(this::onBladeAnimationStart);
    }

    private static final ResourceLocation MotionLocation = new ResourceLocation(SlashBlade.MODID,
            "model/pa/player_motion.vmd");

    public Map<ResourceLocation, VmdAnimation> getAnimation() {
        return animation;
    }

    public void onBladeAnimationStart(BladeMotionEvent event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer))
            return;
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();

        AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(player);

        VmdAnimation animation = this.getAnimation().get(event.getCombo());

        if (animation != null) {
            animationStack.removeLayer(0);
            animation.play();
            animationStack.addAnimLayer(0, animation.getClone());
        }

    }

    private Map<ResourceLocation, VmdAnimation> initAnimations() {
        Map<ResourceLocation, VmdAnimation> map = Maps.newHashMap();

        map.put(ComboStateRegistry.getId(ComboStateRegistry.PIERCING), new VmdAnimation(DefaultResources.testPLLocation, 1, 90, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.PIERCING_JUST), new VmdAnimation(DefaultResources.testPLLocation, 34, 90, false));

        // guard
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1_END2), new VmdAnimation(MotionLocation, 21, 41, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A1), new VmdAnimation(MotionLocation, 1, 41, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A2), new VmdAnimation(MotionLocation, 100, 151, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_C), new VmdAnimation(MotionLocation, 400, 488, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A3), new VmdAnimation(MotionLocation, 200, 306, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A4), new VmdAnimation(MotionLocation, 500, 608, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A4_EX), new VmdAnimation(MotionLocation, 800, 894, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_A5), new VmdAnimation(MotionLocation, 900, 1061, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B1), new VmdAnimation(MotionLocation, 700, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B2), new VmdAnimation(MotionLocation, 710, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B3), new VmdAnimation(MotionLocation, 710, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B4), new VmdAnimation(MotionLocation, 710, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B5), new VmdAnimation(MotionLocation, 710, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B6), new VmdAnimation(MotionLocation, 710, 787, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.COMBO_B7), new VmdAnimation(MotionLocation, 710, 787, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.CIRCLE_SLASH), new VmdAnimation(MotionLocation, 725, 787, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_A1),
                new VmdAnimation(MotionLocation, 1100, 1132, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_A2),
                new VmdAnimation(MotionLocation, 1200, 1241, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_A3),
                new VmdAnimation(MotionLocation, 1300, 1338, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_B3),
                new VmdAnimation(MotionLocation, 1400, 1443, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_RAVE_B4),
                new VmdAnimation(MotionLocation, 1500, 1547, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.UPPERSLASH), new VmdAnimation(MotionLocation, 1600, 1693, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.UPPERSLASH_JUMP),
                new VmdAnimation(MotionLocation, 1700, 1717, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_CLEAVE),
                new VmdAnimation(MotionLocation, 1800, 1817, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_CLEAVE_LOOP),
                new VmdAnimation(MotionLocation, 1812, 1817, true).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.AERIAL_CLEAVE_LANDING), new VmdAnimation(MotionLocation, 1816, 1886, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.RAPID_SLASH),
                new VmdAnimation(MotionLocation, 2000, 2073, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.RAPID_SLASH_QUICK),
                new VmdAnimation(MotionLocation, 2000, 2073, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.RISING_STAR),
                new VmdAnimation(MotionLocation, 2100, 2147, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT),
                new VmdAnimation(MotionLocation, 1900, 1963, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR),
                new VmdAnimation(MotionLocation, 1923, 1963, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST),
                new VmdAnimation(MotionLocation, 1923, 1963, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.VOID_SLASH),
                new VmdAnimation(MotionLocation, 2200, 2299, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_LEFT),
                new VmdAnimation(MotionLocation, 1816, 1859, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_RIGHT),
                new VmdAnimation(MotionLocation, 204, 314, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_LEFT_AIR),
                new VmdAnimation(MotionLocation, 1300, 1328, false).setBlendLegs(false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_RIGHT_AIR),
                new VmdAnimation(MotionLocation, 1200, 1241, false).setBlendLegs(false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.DRIVE_HORIZONTAL), new VmdAnimation(MotionLocation, 400, 488, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.DRIVE_VERTICAL), new VmdAnimation(MotionLocation, 1600, 1693, false));

        map.put(ComboStateRegistry.getId(ComboStateRegistry.WAVE_EDGE_VERTICAL), new VmdAnimation(MotionLocation, 1600, 1693, false));
        map.put(ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_END), new VmdAnimation(MotionLocation, 1923, 1963, false));

        return map;
    }

}
