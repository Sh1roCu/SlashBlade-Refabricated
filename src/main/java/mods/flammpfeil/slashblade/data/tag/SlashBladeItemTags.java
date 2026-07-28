package mods.flammpfeil.slashblade.data.tag;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class SlashBladeItemTags extends FabricTagsProvider.ItemTagsProvider {
    public static final TagKey<Item> PROUD_SOULS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("slashblade", "proudsouls"));
    public static final TagKey<Item> BAMBOO = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "bamboo"));

    public static final TagKey<Item> CAN_COPY_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_sa"));
    public static final TagKey<Item> CAN_COPY_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_copy_se"));
    public static final TagKey<Item> CAN_CHANGE_SA = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_sa"));
    public static final TagKey<Item> CAN_CHANGE_SE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_change_se"));

    public static final TagKey<Item> CAN_REPAIR_BLADE = TagKey.create(Registries.ITEM, SlashBlade.prefix("can_repair_balde"));

    public SlashBladeItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.valueLookupBuilder(ItemTags.SWORDS).add(
                SBItems.SLASHBLADE,
                SBItems.SLASHBLADE_BAMBOO,
                SBItems.SLASHBLADE_SILVERBAMBOO,
                SBItems.SLASHBLADE_WHITE,
                SBItems.SLASHBLADE_WOOD);

        this.valueLookupBuilder(CAN_REPAIR_BLADE)
                .forceAddTag(PROUD_SOULS)
                .forceAddTag(ItemTags.STONE_TOOL_MATERIALS);
    }
}
