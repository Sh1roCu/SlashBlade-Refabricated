package mods.flammpfeil.slashblade.capability.slashblade;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleSlashBladeState extends SlashBladeState {

    private final Identifier model;
    private final Identifier texture;
    private final float attack;
    private int damage;


    public SimpleSlashBladeState(ItemStack blade, Identifier model, Identifier texture, float attack, int damage) {
        super(blade);
        this.model = model;
        this.attack = attack;
        this.damage = damage;
        this.texture = texture;
    }

    @Override
    public @NotNull Optional<Identifier> getModel() {
        return Optional.ofNullable(model);
    }

    @Deprecated
    @Override
    public void setModel(Identifier model) {
    }

    @Override
    public float getBaseAttackModifier() {
        return this.attack;
    }

    @Deprecated
    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
    }

    @Override
    public Identifier getSlashArtsKey() {
        return super.getSlashArtsKey();
    }

    @Deprecated
    @Override
    public void setSlashArtsKey(Identifier key) {
    }

    @Override
    public boolean isDefaultBewitched() {
        return false;
    }

    @Override
    public @NotNull String getTranslationKey() {
        return super.getTranslationKey();
    }

    @Deprecated
    @Override
    public void setTranslationKey(String translationKey) {
    }

    @Override
    public @NotNull Optional<Identifier> getTexture() {
        return Optional.ofNullable(texture);
    }

    @Deprecated
    @Override
    public void setTexture(Identifier texture) {
    }

    @Override
    public int getMaxDamage() {
        return this.damage;
    }

    @Override
    public void setMaxDamage(int damage) {
        super.setMaxDamage(damage);
        this.damage = damage;
    }
}
