package mods.flammpfeil.slashblade.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return SlashBlade.prefix(SlashBlade.MODID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SBItems.slashblade, JEICompat::syncSlashBlade);
    }

    public static String syncSlashBlade(ItemStack stack, UidContext context) {
        // 同步nbt到Cap
        // CCA自动同步
//        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(cap -> {
//            if (stack.getOrCreateTag().contains("bladeState"))
//                cap.deserializeNBT(stack.getOrCreateTag().getCompound("bladeState"));
//        });

        return CapabilitySlashBlade.BLADESTATE.maybeGet(stack).map(ISlashBladeState::getTranslationKey).orElse("");
    }

}
