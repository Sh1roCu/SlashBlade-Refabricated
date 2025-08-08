package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.init.DefaultResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemSlashBladeDetune extends ItemSlashBlade {
    private ResourceLocation model;
    private ResourceLocation texture;
    private final float baseAttack;
    private boolean isDestructable;

    public ItemSlashBladeDetune(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.baseAttack = attackDamageIn;
        this.isDestructable = false;
        this.model = DefaultResources.resourceDefaultModel;
        this.texture = DefaultResources.resourceDefaultTexture;
    }

    public ResourceLocation getModel() {
        return model;
    }

    public ItemSlashBladeDetune setModel(ResourceLocation model) {
        this.model = model;
        return this;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ItemSlashBladeDetune setTexture(ResourceLocation texture) {
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
    public void appendSwordType(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

    }
}
