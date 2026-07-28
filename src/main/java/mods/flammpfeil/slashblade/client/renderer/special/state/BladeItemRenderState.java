package mods.flammpfeil.slashblade.client.renderer.special.state;

import mods.flammpfeil.slashblade.item.SwordType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BladeItemRenderState {
    public static final BladeItemRenderState EMPTY = new BladeItemRenderState();

    public EnumSet<SwordType> swordTypes;
    public Identifier modelLocation;
    public Identifier textureLocation;
    public boolean hasFoil;
    public int damageValue;
    public int maxDamage;
    public boolean isFramed = false;
    public @Nullable BladeStandState bladeStandState = null;

    public static class BladeStandState {
        public Item currentType;
        public Pose pose;
    }
}
