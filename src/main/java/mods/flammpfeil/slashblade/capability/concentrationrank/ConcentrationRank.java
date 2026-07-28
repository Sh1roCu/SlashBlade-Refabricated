package mods.flammpfeil.slashblade.capability.concentrationrank;

import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public float getRankPointModifier(Identifier combo) {
        return 0.1f;
    }

    @Override
    public void readData(ValueInput readView) {
        this.setRawRankPoint(readView.getLongOr("rawPoint", 0));
        this.setLastUpdate(readView.getLongOr("lastupdate", 0));
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putLong("rawPoint", this.getRawRankPoint());
        writeView.putLong("lastupdate", this.getLastUpdate());
    }
}
