package mods.flammpfeil.slashblade.client.renderer.model;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBakedItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeModel extends WrapperBakedItemModel {

    public BladeModel(ItemModel original, ItemModel.BakingContext loader) {
        super(original);
    }

    public static LivingEntity user = null;
}
