package mods.flammpfeil.slashblade.compat.emi;

import dev.emi.emi.api.stack.Comparison;
import net.minecraft.world.item.ItemStack;

public class EMIUtils {

    public static Comparison SLASHBLADE_COMPARISON = Comparison.of((self, other) -> {
        ItemStack aStack = self.getItemStack();
        ItemStack bStack = other.getItemStack();
        if (aStack.getItem() != bStack.getItem()) return false;
        String keyA = self.getNbt().getCompound("bladeState").getString("translationKey");
        String keyB = other.getNbt().getCompound("bladeState").getString("translationKey");

        return keyB.equals(keyA);
    });
}