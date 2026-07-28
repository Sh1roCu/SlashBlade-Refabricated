package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public class ItemTierSlashBlade {

    private final int uses;
    private final float attack;

    public ItemTierSlashBlade(int uses, float attack) {
        this.attack = attack;
        this.uses = uses;
    }

    public int getUses() {
        return uses;
    }

    public float getSpeed() {
        return 0;
    }

    public float getAttackDamageBonus() {
        return attack;
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; // 对应旧版的getLevel
    }

    public int getEnchantmentValue() {
        return 10;
    }

    public TagKey<Item> getRepairItems() {
        return SlashBladeItemTags.PROUD_SOULS;
    }

    public ToolMaterial toToolMaterial() {
        return new ToolMaterial(getIncorrectBlocksForDrops(), getUses(), getSpeed(), getAttackDamageBonus(), getEnchantmentValue(), getRepairItems());
    }
}
