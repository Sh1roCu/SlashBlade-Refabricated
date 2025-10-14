package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class ItemTierSlashBlade implements Tier {

    private final int uses;
    private final float attack;

    public ItemTierSlashBlade(int uses, float attack) {
        this.attack = attack;
        this.uses = uses;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public float getAttackDamageBonus() {
        return attack;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; // 对应旧版的getLevel
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(SlashBladeItemTags.PROUD_SOULS);
    }
}
