package mods.flammpfeil.slashblade.data.tag;

import mods.flammpfeil.slashblade.SlashBlade;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class SlashBladeEntityTypeTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public SlashBladeEntityTypeTagProvider(FabricPackOutput output, CompletableFuture<Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(Provider lookupProvider) {
        this.builder(EntityTypeTags.ATTACKABLE_BLACKLIST)
                .add(EntityType.VILLAGER.builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("touhou_little_maid", "maid")));

        this.builder(EntityTypeTags.RENDER_LAYER_BLACKLIST)
                .addOptional(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("touhou_little_maid", "maid")));
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> ATTACKABLE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
                SlashBlade.prefix("blacklist/attackable"));
        public static final TagKey<EntityType<?>> RENDER_LAYER_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
                SlashBlade.prefix("blacklist/render_layer"));
    }
}
