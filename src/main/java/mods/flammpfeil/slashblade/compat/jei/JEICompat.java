package mods.flammpfeil.slashblade.compat.jei;

import cn.sh1rocu.slashblade.client.ClientRecipeEvent;
import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.common.Internal;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.recipe.RecipeSerializerRegistry;
import mods.flammpfeil.slashblade.recipe.SlashBladeSmithingRecipe;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return SlashBlade.prefix(SlashBlade.MODID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SBItems.SLASHBLADE, new ISubtypeInterpreter<>() {
            @Override
            public @NotNull Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return syncSlashBlade(ingredient, context);
            }

        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (ClientRecipeEvent.SB_RECIPES.isEmpty()) {
            ClientRecipeEvent.SB_RECIPES = Lists.newArrayList(Internal.getClientSyncedRecipes().byType(RecipeSerializerRegistry.SLASHBLADE_SHAPED_TYPE));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, ClientRecipeEvent.SB_RECIPES);
    }

    public static String syncSlashBlade(ItemStack stack, UidContext context) {
        var cap = CapabilitySlashBlade.getBladeState(stack);
        cap.ifPresent(state -> state.setDamage(0));
        return cap.map(ISlashBladeState::getTranslationKey).orElse("");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        var smithingCategory = registration.getSmithingCategory();

        smithingCategory.addExtension(SlashBladeSmithingRecipe.class, new SlashBladeSmithingCategoryExtension());
    }
}
