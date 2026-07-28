package mods.flammpfeil.slashblade.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;

public class SlashBladeShapedRecipeSerializer  {
    public static final MapCodec<SlashBladeShapedRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(SlashBladeShapedRecipe::getGroup),
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(SlashBladeShapedRecipe::category),
            ShapedRecipePattern.MAP_CODEC.forGetter(SlashBladeShapedRecipe::getPattern),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(SlashBladeShapedRecipe::showNotification),
            Identifier.CODEC.fieldOf("blade").forGetter(SlashBladeShapedRecipe::getOutputBlade)
    ).apply(instance, SlashBladeShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlashBladeShapedRecipe> STREAM_CODEC = StreamCodec.of(
            SlashBladeShapedRecipeSerializer::toNetwork, SlashBladeShapedRecipeSerializer::fromNetwork);

    @NotNull
    public static SlashBladeShapedRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buf) {
        String group = buf.readUtf();
        CraftingBookCategory craftingBookCategory = buf.readEnum(CraftingBookCategory.class);
        ShapedRecipePattern shapedRecipePattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
        ItemStackTemplate itemStack = ItemStackTemplate.STREAM_CODEC.decode(buf);
        boolean showNotification = buf.readBoolean();
        Identifier blade = buf.readIdentifier();
        return new SlashBladeShapedRecipe(group, craftingBookCategory, shapedRecipePattern, itemStack, showNotification, blade);
    }

    public static void toNetwork(@NotNull RegistryFriendlyByteBuf buf, @NotNull SlashBladeShapedRecipe recipe) {
        buf.writeUtf(recipe.group);
        buf.writeEnum(recipe.category);
        ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
        buf.writeBoolean(recipe.showNotification);
        buf.writeIdentifier(recipe.outputBlade);
    }
}