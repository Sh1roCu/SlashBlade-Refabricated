package mods.flammpfeil.slashblade.data.tag;

import mods.flammpfeil.slashblade.SlashBlade;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class SlashBladeEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public SlashBladeEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(Provider lookupProvider) {
        this.getOrCreateTagBuilder(EntityTypeTags.ATTACKABLE_BLACKLIST)
                .add(EntityType.VILLAGER)
                .addOptional(new ResourceLocation("touhou_little_maid", "maid"));
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> ATTACKABLE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
                SlashBlade.prefix("blacklist/attackable"));
    }
}
