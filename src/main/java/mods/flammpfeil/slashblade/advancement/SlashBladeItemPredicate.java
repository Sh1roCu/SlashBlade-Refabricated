package mods.flammpfeil.slashblade.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jspecify.annotations.NonNull;

public class SlashBladeItemPredicate implements DataComponentPredicate {
    public static final Codec<SlashBladeItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RequestDefinition.CODEC.fieldOf("requestBlade").forGetter(SlashBladeItemPredicate::getRequest),
            Item.CODEC.fieldOf("bladeItem").forGetter(SlashBladeItemPredicate::getBlade)
    ).apply(instance, SlashBladeItemPredicate::new));

    private final RequestDefinition request;
    private final Holder<Item> blade;

    public SlashBladeItemPredicate(RequestDefinition request, Holder<Item> blade) {
        this.request = request;
        this.blade = blade;
    }

    public RequestDefinition getRequest() {
        return request;
    }

    public Holder<Item> getBlade() {
        return blade;
    }

    @Override
    public boolean matches(@NonNull DataComponentGetter components) {
        var name = this.getRequest().getName();
        ItemStack stack = this.blade.value().getDefaultInstance();
        stack.set(CapabilitySlashBlade.BLADESTATE_COMPONENT, components.getOrDefault(CapabilitySlashBlade.BLADESTATE_COMPONENT, CustomData.EMPTY));
        stack.set(DataComponents.ENCHANTMENTS, components.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        stack.set(DataComponents.CUSTOM_NAME, components.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()));
        var unbreakable = components.get(DataComponents.UNBREAKABLE);
        if (unbreakable != null) {
            stack.set(DataComponents.UNBREAKABLE, unbreakable);
        }
        boolean requestCheck = this.getRequest().test(stack);
        if (name.equals(SlashBlade.prefix("none")))
            return requestCheck && stack.is(SBItems.SLASHBLADE);
        if (BuiltInRegistries.ITEM.containsKey(name)) {
            return requestCheck && stack.is(BuiltInRegistries.ITEM.getValue(name));
        }
        return requestCheck && (stack.getItem() instanceof ItemSlashBlade);
    }
}