package mods.flammpfeil.slashblade.data.tag;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SlashBladeItemTags {
    public static final TagKey<Item> PROUD_SOULS = TagKey.create(Registries.ITEM, new ResourceLocation("slashblade", "proudsouls"));
    public static final TagKey<Item> BAMBOO = TagKey.create(Registries.ITEM, new ResourceLocation("c", "bamboo"));

    public static final TagKey<Item> CAN_COPY_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_sa"));
    public static final TagKey<Item> CAN_COPY_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_se"));
    public static final TagKey<Item> CAN_CHANGE_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_sa"));
    public static final TagKey<Item> CAN_CHANGE_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_se"));
}
