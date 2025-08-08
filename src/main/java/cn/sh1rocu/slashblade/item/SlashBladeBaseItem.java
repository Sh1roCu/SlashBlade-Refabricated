package cn.sh1rocu.slashblade.item;

import cn.sh1rocu.slashblade.api.extension.BaseItemExtension;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class SlashBladeBaseItem extends Item implements BaseItemExtension {
    public SlashBladeBaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }
}
