package mods.flammpfeil.slashblade.registry;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;

public class SlashArtsRegistry {
    public static void init() {

    }

    public static final Registry<SlashArts> SLASH_ARTS = FabricRegistryBuilder
            .createSimple(SlashArts.REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final SlashArts NONE = Registry.register(SLASH_ARTS, SlashBlade.prefix("none"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.NONE)));

    public static final SlashArts JUDGEMENT_CUT = Registry.register(SLASH_ARTS, SlashBlade.prefix("judgement_cut"),
            new SlashArts((e) -> e.onGround() ? ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT)
                    : ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR)).setComboStateJust((e) -> ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST)).setComboStateSuper(e -> ComboStateRegistry.getId(ComboStateRegistry.JUDGEMENT_CUT_END)));

    public static final SlashArts SAKURA_END = Registry.register(SLASH_ARTS, SlashBlade.prefix("sakura_end"),
            new SlashArts((e) -> e.onGround() ? ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_LEFT)
                    : ComboStateRegistry.getId(ComboStateRegistry.SAKURA_END_LEFT_AIR)));

    public static final SlashArts VOID_SLASH = Registry.register(SLASH_ARTS, SlashBlade.prefix("void_slash"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.VOID_SLASH)));

    public static final SlashArts CIRCLE_SLASH = Registry.register(SLASH_ARTS, SlashBlade.prefix("circle_slash"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.CIRCLE_SLASH)));

    public static final SlashArts DRIVE_VERTICAL = Registry.register(SLASH_ARTS, SlashBlade.prefix("drive_vertical"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.DRIVE_VERTICAL)));

    public static final SlashArts DRIVE_HORIZONTAL = Registry.register(SLASH_ARTS, SlashBlade.prefix("drive_horizontal"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.DRIVE_HORIZONTAL)));

    public static final SlashArts WAVE_EDGE = Registry.register(SLASH_ARTS, SlashBlade.prefix("wave_edge"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.WAVE_EDGE_VERTICAL)));

    public static final SlashArts PIERCING = Registry.register(SLASH_ARTS, SlashBlade.prefix("piercing"),
            new SlashArts((e) -> ComboStateRegistry.getId(ComboStateRegistry.PIERCING))
                    .setComboStateJust((e) -> ComboStateRegistry.getId(ComboStateRegistry.PIERCING_JUST))
    );
}
