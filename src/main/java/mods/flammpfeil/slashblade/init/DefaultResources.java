package mods.flammpfeil.slashblade.init;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.Identifier;

public interface DefaultResources {
    Identifier BaseMotionLocation = SlashBlade.prefix("combostate/old_motion.vmd");
    Identifier ExMotionLocation = SlashBlade.prefix("combostate/motion.vmd");

    Identifier testLocation = SlashBlade.prefix("combostate/piercing.vmd");

    Identifier testPLLocation = SlashBlade.prefix("combostate/piercing_pl.vmd");

    Identifier resourceDefaultModel = Identifier.fromNamespaceAndPath("slashblade", "model/blade.obj");
    Identifier resourceDefaultTexture = Identifier.fromNamespaceAndPath("slashblade", "model/blade.png");

    Identifier resourceDurabilityModel = Identifier.fromNamespaceAndPath("slashblade",
            "model/util/durability.obj");
    Identifier resourceDurabilityTexture = Identifier.fromNamespaceAndPath("slashblade",
            "model/util/durability.png");
}
