package mods.flammpfeil.slashblade.capability.concentrationrank;

import mods.flammpfeil.slashblade.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class ConcentrationRank implements IConcentrationRank {

    long rankpoint;
    long lastupdate;
    long lastrankrise;

    public static long UnitCapacity = 300;

    public ConcentrationRank() {
        rankpoint = 0;
        lastupdate = 0;
    }

    @Override
    public long getRawRankPoint() {
        return rankpoint;
    }

    @Override
    public void setRawRankPoint(long point) {
        this.rankpoint = point;
    }

    @Override
    public long getLastUpdate() {
        return lastupdate;
    }

    @Override
    public void setLastUpdate(long time) {
        this.lastupdate = time;
    }

    @Override
    public long getLastRankRise() {
        return this.lastrankrise;
    }

    @Override
    public void setLastRankRise(long time) {
        this.lastrankrise = time;
    }

    @Override
    public long getUnitCapacity() {
        return UnitCapacity;
    }

    @Override
    public float getRankPointModifier(DamageSource ds) {
        return 0.1f;
    }

    @Override
    public float getRankPointModifier(ResourceLocation combo) {
        return 0.1f;
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        NBTHelper.getNBTCoupler(tag)
                .get("rawPoint", this::setRawRankPoint).get("lastupdate", this::setLastUpdate);
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        NBTHelper.getNBTCoupler(tag).put("rawPoint", getRawRankPoint()).put("lastupdate", getLastUpdate());
    }
}
