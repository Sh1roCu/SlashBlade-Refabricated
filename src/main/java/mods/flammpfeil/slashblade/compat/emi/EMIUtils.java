//package mods.flammpfeil.slashblade.compat.emi;
//
//import dev.emi.emi.api.stack.Comparison;
//import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
//import net.minecraft.core.component.DataComponents;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.component.CustomData;
//
//public class EMIUtils {
//
//    public static Comparison SLASHBLADE_COMPARISON = Comparison.of((self, other) -> {
//        ItemStack aStack = self.getItemStack();
//        ItemStack bStack = other.getItemStack();
//        if (aStack.getItem() != bStack.getItem()) return false;
//        String keyA = self.getOrDefault(CapabilitySlashBlade.BLADESTATE_COMPONENT, CustomData.EMPTY).copyTag().getString("translationKey");
//        String keyB = other.getOrDefault(CapabilitySlashBlade.BLADESTATE_COMPONENT, CustomData.EMPTY).copyTag().getString("translationKey");
//
//        return keyB.equals(keyA);
//    });
//}