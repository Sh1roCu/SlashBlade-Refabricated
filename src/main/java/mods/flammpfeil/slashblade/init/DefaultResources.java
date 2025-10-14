package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.ResourceLocation;

public interface DefaultResources {
    ResourceLocation BaseMotionLocation = SlashBlade.prefix("combostate/old_motion.vmd");
    ResourceLocation ExMotionLocation = SlashBlade.prefix("combostate/motion.vmd");

    ResourceLocation testLocation = SlashBlade.prefix("combostate/piercing.vmd");

    ResourceLocation testPLLocation = SlashBlade.prefix("combostate/piercing_pl.vmd");

    ResourceLocation resourceDefaultModel = ResourceLocation.fromNamespaceAndPath("slashblade", "model/blade.obj");
    ResourceLocation resourceDefaultTexture = ResourceLocation.fromNamespaceAndPath("slashblade", "model/blade.png");

    ResourceLocation resourceDurabilityModel = ResourceLocation.fromNamespaceAndPath("slashblade",
            "model/util/durability.obj");
    ResourceLocation resourceDurabilityTexture = ResourceLocation.fromNamespaceAndPath("slashblade",
            "model/util/durability.png");
}
