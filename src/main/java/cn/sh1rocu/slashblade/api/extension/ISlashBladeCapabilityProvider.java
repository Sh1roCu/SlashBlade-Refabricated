package cn.sh1rocu.slashblade.api.extension;

import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import net.minecraft.world.item.ItemStack;

public interface ISlashBladeCapabilityProvider {
    /**
     * Override this method to register different Capability(CCA).
     * <p>
     * 重写该方法来注册不同的能力（Fabric的CCA），例如消耗Energy的HF Blade
     */
    SlashBladeState initCapability(ItemStack stack);
}
