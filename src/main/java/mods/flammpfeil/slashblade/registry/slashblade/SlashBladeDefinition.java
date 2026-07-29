package mods.flammpfeil.slashblade.registry.slashblade;

import cn.sh1rocu.slashblade.mixin.accessor.VanillaRegistriesAccessor;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeCreativeGroup;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.data.builtin.SlashBladeBuiltInRegistry;
import mods.flammpfeil.slashblade.event.SlashBladeRegistryEvent;
import mods.flammpfeil.slashblade.init.SBItems;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class SlashBladeDefinition {

    public static final Codec<SlashBladeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("item", SlashBlade.prefix("slashblade"))
                            .forGetter(SlashBladeDefinition::getItemName),
                    Identifier.CODEC.fieldOf("name").forGetter(SlashBladeDefinition::getName),
                    RenderDefinition.CODEC.fieldOf("render").forGetter(SlashBladeDefinition::getRenderDefinition),
                    PropertiesDefinition.CODEC.fieldOf("properties").forGetter(SlashBladeDefinition::getStateDefinition),
                    EnchantmentDefinition.CODEC.listOf().optionalFieldOf("enchantments", Lists.newArrayList())
                            .forGetter(SlashBladeDefinition::getEnchantments),
                    Identifier.CODEC.optionalFieldOf("creativeGroup", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(SlashBladeCreativeGroup.SLASHBLADE_GROUP))
                            .forGetter(SlashBladeDefinition::getCreativeGroup))
            .apply(instance, SlashBladeDefinition::new));

    public static final ResourceKey<Registry<SlashBladeDefinition>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(SlashBlade.prefix("named_blades"));

    private final Identifier item;
    private final Identifier name;
    private final RenderDefinition renderDefinition;
    private final PropertiesDefinition stateDefinition;
    private final List<EnchantmentDefinition> enchantments;

    private final Identifier creativeGroup;

    public SlashBladeDefinition(Identifier name, RenderDefinition renderDefinition,
                                PropertiesDefinition stateDefinition, List<EnchantmentDefinition> enchantments) {
        this(SlashBlade.prefix("slashblade"), name, renderDefinition, stateDefinition, enchantments,
                BuiltInRegistries.CREATIVE_MODE_TAB.getKey(SlashBladeCreativeGroup.SLASHBLADE_GROUP));
    }

    public SlashBladeDefinition(Identifier name, RenderDefinition renderDefinition,
                                PropertiesDefinition stateDefinition, List<EnchantmentDefinition> enchantments,
                                Identifier creativeGroup) {
        this(SlashBlade.prefix("slashblade"), name, renderDefinition, stateDefinition, enchantments, creativeGroup);
    }

    public SlashBladeDefinition(Identifier item, Identifier name, RenderDefinition renderDefinition,
                                PropertiesDefinition stateDefinition, List<EnchantmentDefinition> enchantments) {
        this(item, name, renderDefinition, stateDefinition, enchantments,
                BuiltInRegistries.CREATIVE_MODE_TAB.getKey(SlashBladeCreativeGroup.SLASHBLADE_GROUP));
    }

    public SlashBladeDefinition(Identifier item, Identifier name, RenderDefinition renderDefinition,
                                PropertiesDefinition stateDefinition, List<EnchantmentDefinition> enchantments,
                                Identifier creativeGroup) {
        this.item = item;
        this.name = name;
        this.renderDefinition = renderDefinition;
        this.stateDefinition = stateDefinition;
        this.enchantments = enchantments;
        this.creativeGroup = creativeGroup;
    }

    public Identifier getItemName() {
        return item;
    }

    public Identifier getName() {
        return name;
    }

    public String getTranslationKey() {
        return Util.makeDescriptionId("item", this.getName());
    }

    public RenderDefinition getRenderDefinition() {
        return renderDefinition;
    }

    public PropertiesDefinition getStateDefinition() {
        return stateDefinition;
    }

    public List<EnchantmentDefinition> getEnchantments() {
        return enchantments;
    }

    public ItemStack getBlade() {
        return getBlade(getItem());
    }

    public ItemStack getBlade(Item bladeItem) {
        SlashBladeRegistryEvent.Pre event = new SlashBladeRegistryEvent.Pre(this);
        SlashBladeRegistryEvent.PRE.invoker().onPre(event);
        if (event.isCanceled())
            return ItemStack.EMPTY;

        ItemStack result = new ItemStack(bladeItem);
        var state = CapabilitySlashBlade.getBladeState(result).orElse(new SlashBladeState(result));
        state.setNonEmpty();
        state.setBaseAttackModifier(this.stateDefinition.getBaseAttackModifier());
        state.setMaxDamage(this.stateDefinition.getMaxDamage());
        state.setComboRoot(this.stateDefinition.getComboRoot());
        state.setSlashArtsKey(this.stateDefinition.getSpecialAttackType());

        this.stateDefinition.getSpecialEffects().forEach(state::addSpecialEffect);

        this.stateDefinition.getDefaultType().forEach(type -> {
            switch (type) {
                case BEWITCHED -> state.setDefaultBewitched(true);
                case BROKEN -> {
                    result.setDamageValue(result.getMaxDamage() - 1);
                    state.setBroken(true);
                }
                case SEALED -> state.setSealed(true);
                default -> {
                }
            }
        });

        state.setModel(this.renderDefinition.getModelName());
        state.setTexture(this.renderDefinition.getTextureName());
        state.setColorCode(this.renderDefinition.getSummonedSwordColor());
        state.setEffectColorInverse(this.renderDefinition.isSummonedSwordColorInverse());
        state.setCarryType(this.renderDefinition.getStandbyRenderType());
        if (!this.getName().equals(SlashBlade.prefix("none")))
            state.setTranslationKey(this.getTranslationKey());

        for (var instance : this.enchantments) {
            var enchantment = instance.getEnchantment();
            result.enchant(enchantment, instance.getEnchantmentLevel());

        }
        if (this.stateDefinition.isUnbreakable())
            result.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        var postRegistry = new SlashBladeRegistryEvent.Post(this, result);
        SlashBladeRegistryEvent.POST.invoker().onPost(postRegistry);
        return postRegistry.getBlade();
    }

    public Item getItem() {
        if (BuiltInRegistries.ITEM.containsKey(this.item))
            return BuiltInRegistries.ITEM.getValue(this.item);

        return SBItems.SLASHBLADE;
    }

    public Identifier getCreativeGroup() {
        return creativeGroup;
    }

    public static final BladeComparator COMPARATOR = new BladeComparator();

    private static class BladeComparator implements Comparator<Reference<SlashBladeDefinition>> {
        @Override
        public int compare(Reference<SlashBladeDefinition> left, Reference<SlashBladeDefinition> right) {

            Identifier leftKey = left.key().identifier();
            Identifier rightKey = right.key().identifier();
            boolean checkSame = leftKey.getNamespace().equalsIgnoreCase(rightKey.getNamespace());
            if (!checkSame) {
                if (leftKey.getNamespace().equalsIgnoreCase(SlashBlade.MODID))
                    return -1;

                if (rightKey.getNamespace().equalsIgnoreCase(SlashBlade.MODID))
                    return 1;
            }
            String leftName = leftKey.toString();
            String rightName = rightKey.toString();

            return leftName.compareToIgnoreCase(rightName);
        }
    }
}
