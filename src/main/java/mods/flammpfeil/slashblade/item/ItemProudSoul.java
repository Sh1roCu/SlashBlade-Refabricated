package mods.flammpfeil.slashblade.item;

import cn.sh1rocu.slashblade.api.extension.BaseItemExtension;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ItemProudSoul extends Item implements BaseItemExtension {
    public ItemProudSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack item) {
        return true;
    }

}
