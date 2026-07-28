package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.capability.slashblade.SimpleSlashBladeState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.init.DefaultResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public class ItemSlashBladeDetune extends ItemSlashBlade {
    private Identifier model;
    private Identifier texture;
    private final float baseAttack;
    private boolean isDestructable;

    public ItemSlashBladeDetune(ToolMaterial tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.baseAttack = attackDamageIn;
        this.isDestructable = false;
        this.model = DefaultResources.resourceDefaultModel;
        this.texture = DefaultResources.resourceDefaultTexture;
    }

    public Identifier getModel() {
        return model;
    }

    public ItemSlashBladeDetune setModel(Identifier model) {
        this.model = model;
        return this;
    }

    public Identifier getTexture() {
        return texture;
    }

    public ItemSlashBladeDetune setTexture(Identifier texture) {
        this.texture = texture;
        return this;
    }

    public float getBaseAttack() {
        return baseAttack;
    }

    public boolean isDestructable() {
        return isDestructable;
    }

    public ItemSlashBladeDetune setDestructable() {
        this.isDestructable = true;
        return this;
    }

    @Override
    public boolean isDestructable(ItemStack stack) {
        return this.isDestructable;
    }

    @Override
    public void appendSwordType(ItemStack stack, TooltipContext context, Consumer<Component> tooltip, TooltipFlag flagIn) {

    }

    @Override
    public SlashBladeState initCapability(ItemStack stack) {
        return new SimpleSlashBladeState(stack, this.getModel(), this.getTexture(), this.getBaseAttack(), this.tier.durability());
    }
}
