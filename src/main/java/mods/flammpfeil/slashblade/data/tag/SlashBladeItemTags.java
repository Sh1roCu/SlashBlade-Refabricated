package mods.flammpfeil.slashblade.data.tag;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class SlashBladeItemTags extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> PROUD_SOULS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("slashblade", "proudsouls"));
    public static final TagKey<Item> BAMBOO = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "bamboo"));

    public static final TagKey<Item> CAN_COPY_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_sa"));
    public static final TagKey<Item> CAN_COPY_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_se"));
    public static final TagKey<Item> CAN_CHANGE_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_sa"));
    public static final TagKey<Item> CAN_CHANGE_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_se"));

    public SlashBladeItemTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.getOrCreateTagBuilder(ItemTags.SWORDS).add(
                SBItems.slashblade,
                SBItems.slashblade_bamboo,
                SBItems.slashblade_silverbamboo,
                SBItems.slashblade_white,
                SBItems.slashblade_wood);
    }
}
