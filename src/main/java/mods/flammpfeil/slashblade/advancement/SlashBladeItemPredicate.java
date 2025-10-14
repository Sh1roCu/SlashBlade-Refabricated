package mods.flammpfeil.slashblade.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlashBladeItemPredicate implements ItemSubPredicate {
    public static final Codec<SlashBladeItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RequestDefinition.CODEC.fieldOf("requestBlade").forGetter(SlashBladeItemPredicate::getRequest)
    ).apply(instance, SlashBladeItemPredicate::new));
    public static final ItemSubPredicate.Type<SlashBladeItemPredicate> TYPE = new ItemSubPredicate.Type<>(CODEC);

    private final RequestDefinition request;

    public SlashBladeItemPredicate(RequestDefinition request) {
        this.request = request;
    }

    @Override
    public boolean matches(@NotNull ItemStack stack) {
        var name = this.getRequest().getName();
        boolean requestCheck = this.getRequest().test(stack);
        if (name.equals(SlashBlade.prefix("none")))
            return requestCheck && stack.is(SBItems.slashblade);
        if (BuiltInRegistries.ITEM.containsKey(name)) {
            return requestCheck && stack.is(BuiltInRegistries.ITEM.get(name));
        }
        return requestCheck && (stack.getItem() instanceof ItemSlashBlade);
    }

    public RequestDefinition getRequest() {
        return request;
    }
}