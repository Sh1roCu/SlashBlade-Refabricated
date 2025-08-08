package mods.flammpfeil.slashblade.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class SlashBladeItemTask extends Task implements Predicate<ItemStack> {
    private static final String BLANK_NAME = "slashblade:none";
    public static TaskType TYPE;
    // TODO 根据Name还原ItemStack（是否只需要客户端还需要测试）并进行测试
    // TODO 越看FTB代码越感觉抽象就不继续写了。
    private String name;
    private int proudSoulCount;
    private int killCount;
    private int refineCount;

    private Tristate consumeItems;
    private Tristate onlyFromCrafting;
    private boolean taskScreenOnly;

    public SlashBladeItemTask(long id, Quest quest) {
        super(id, quest);
        consumeItems = Tristate.DEFAULT;
        onlyFromCrafting = Tristate.DEFAULT;
        taskScreenOnly = false;
    }

    @Override
    public TaskType getType() {
        return TYPE;
    }

    public String getName() {
        return name;
    }

    public int getProudSoulCount() {
        return proudSoulCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getRefineCount() {
        return refineCount;
    }

    public String getTranslationKey() {
        return Util.makeDescriptionId("item", ResourceLocation.tryParse(this.getName()));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        config.addString("name", name, v -> name = v, BLANK_NAME).setNameKey("ftbquests.task.slashblade.name");
        config.addInt("proudSoulCount", proudSoulCount, v -> proudSoulCount = v, 0, 0, Integer.MAX_VALUE);
        config.addInt("killCount", killCount, v -> killCount = v, 0, 0, Integer.MAX_VALUE);
        config.addInt("refineCount", refineCount, v -> refineCount = v, 0, 0, Integer.MAX_VALUE);
        config.addEnum("consume_items", consumeItems, v -> consumeItems = v, Tristate.NAME_MAP);
        config.addEnum("only_from_crafting", onlyFromCrafting, v -> onlyFromCrafting = v, Tristate.NAME_MAP);
        config.addBool("task_screen_only", taskScreenOnly, v -> taskScreenOnly = v, false);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return true;
        }

        if (CapabilitySlashBlade.BLADESTATE.maybeGet(itemStack).isEmpty())
            return false;
        var state = CapabilitySlashBlade.BLADESTATE.maybeGet(itemStack).orElseThrow(NullPointerException::new);
        boolean nameCheck;
        if (this.name.equals(BLANK_NAME)) {
            nameCheck = state.getTranslationKey().isBlank();
        } else {
            nameCheck = state.getTranslationKey().equals(this.getTranslationKey());
        }
        boolean proudCheck = state.getProudSoulCount() >= this.getProudSoulCount();
        boolean killCheck = state.getKillCount() >= this.getKillCount();
        boolean refineCheck = state.getRefine() >= this.getRefineCount();

        return nameCheck && proudCheck && killCheck && refineCheck;
    }
}
