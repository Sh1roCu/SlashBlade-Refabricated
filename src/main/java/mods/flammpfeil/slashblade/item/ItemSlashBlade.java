package mods.flammpfeil.slashblade.item;

import cn.sh1rocu.slashblade.api.extension.EntityExtension;
import cn.sh1rocu.slashblade.api.extension.IEnchantment;
import cn.sh1rocu.slashblade.api.extension.ItemSlashBladeExtension;
import com.google.common.collect.*;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.SlashBladeTEISR;
import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.entity.BladeItemEntity;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemSlashBlade extends SwordItem implements IEnchantment, ItemSlashBladeExtension {
    protected static final UUID ATTACK_DAMAGE_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FD");
    protected static final UUID PLAYER_REACH_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FE");

    public static final List<Enchantment> exEnchantment = List.of(Enchantments.SOUL_SPEED, Enchantments.POWER_ARROWS,
            Enchantments.FALL_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.THORNS);

    public ItemSlashBlade(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (exEnchantment.contains(enchantment))
            return true;
        return IEnchantment.super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> def = super.getAttributeModifiers(stack, slot);
        Multimap<Attribute, AttributeModifier> result = ArrayListMultimap.create();

        result.putAll(Attributes.ATTACK_DAMAGE, def.get(Attributes.ATTACK_DAMAGE));
        result.putAll(Attributes.ATTACK_SPEED, def.get(Attributes.ATTACK_SPEED));

        if (slot == EquipmentSlot.MAINHAND) {
            Optional<ISlashBladeState> state = CapabilitySlashBlade.BLADESTATE.maybeGet(stack);
            state.ifPresent(s -> {
                // 刀的状态
                var swordType = SwordType.from(stack);
                // 获得基础攻击力
                float baseAttackModifier = s.getBaseAttackModifier();
                // 锻造数
                int refine = s.getRefine();

                float attackAmplifier = s.getAttackAmplifier();
                if (s.isBroken()) {
                    // 断刀-0.5伤害
                    attackAmplifier = -0.5F - baseAttackModifier;
                } else {
                    float refineFactor = swordType.contains(SwordType.FIERCEREDGE) ? 0.1F : 0.05F;
                    // 锻造伤害面板增加计算，非线性，收益递减。(理论最大值为额外100%基础攻击)
                    attackAmplifier = (1.0F - (1.0F / (1.0F + (refineFactor * refine)))) * baseAttackModifier;
                }

                double damage = (double) baseAttackModifier + attackAmplifier - 1F;

                var event = new SlashBladeEvent.UpdateAttackEvent(stack, s, damage);
                SlashBladeEvent.UPDATE_ATTACK.invoker().onUpdateAttack(event);

                AttributeModifier attack = new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        event.getNewDamage(), AttributeModifier.Operation.ADDITION);

                result.remove(Attributes.ATTACK_DAMAGE, attack);
                result.put(Attributes.ATTACK_DAMAGE, attack);

                result.put(ReachEntityAttributes.REACH,
                        new AttributeModifier(PLAYER_REACH_AMPLIFIER, "Reach amplifer",
                                s.isBroken() ? ReachModifier.BrokendReach() : ReachModifier.BladeReach(),
                                AttributeModifier.Operation.ADDITION));

            });
        }

        return result;
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack stack) {
        EnumSet<SwordType> type = SwordType.from(stack);
        if (type.contains(SwordType.BEWITCHED))
            return Rarity.EPIC;
        if (type.contains(SwordType.ENCHANTED))
            return Rarity.RARE;
        return Rarity.COMMON;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (handIn == InteractionHand.OFF_HAND && !(playerIn.getMainHandItem().getItem() instanceof ItemSlashBlade)) {
            return InteractionResultHolder.pass(itemstack);
        }
        boolean result = CapabilitySlashBlade.BLADESTATE.maybeGet(itemstack).map((state) -> {

            CapabilityInputState.INPUT_STATE.maybeGet(playerIn).ifPresent((s) -> s.getCommands().add(InputCommand.R_CLICK));

            ResourceLocation combo = state.progressCombo(playerIn);

            CapabilityInputState.INPUT_STATE.maybeGet(playerIn).ifPresent((s) -> s.getCommands().remove(InputCommand.R_CLICK));

            if (!combo.equals(ComboStateRegistry.getId(ComboStateRegistry.NONE)))
                playerIn.swing(handIn);

            return true;
        }).orElse(false);

        playerIn.startUsingItem(handIn);
        return new InteractionResultHolder<>(result ? InteractionResult.SUCCESS : InteractionResult.FAIL, itemstack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemstack, Player playerIn, Entity entity) {
        Optional<ISlashBladeState> stateHolder = CapabilitySlashBlade.BLADESTATE.maybeGet(itemstack)
                .filter((state) -> !state.onClick());

        stateHolder.ifPresent((state) -> {
            CapabilityInputState.INPUT_STATE.maybeGet(playerIn).ifPresent((s) -> s.getCommands().add(InputCommand.L_CLICK));

            state.progressCombo(playerIn);

            CapabilityInputState.INPUT_STATE.maybeGet(playerIn).ifPresent((s) -> s.getCommands().remove(InputCommand.L_CLICK));
        });

        return stateHolder.isPresent();
    }

    public static final String BREAK_ACTION_TIMEOUT = "BreakActionTimeout";

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int maxDamage = stack.getMaxDamage();
        var state = CapabilitySlashBlade.BLADESTATE.maybeGet(stack).orElseThrow(NullPointerException::new);
        if (state.isBroken()) {
            if (damage <= 0 && !state.isSealed()) {
                state.setBroken(false);
            } else if (maxDamage < damage) {
                damage = Math.min(damage, maxDamage - 1);
            }
        }
        state.setDamage(damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (amount <= 0)
            return 0;

        var cap = CapabilitySlashBlade.BLADESTATE.maybeGet(stack).orElseThrow(NullPointerException::new);
        boolean current = cap.isBroken();

        if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            amount = 0;
            stack.setDamageValue(stack.getMaxDamage() - 1);
            SlashBladeEvent.BreakEvent event = new SlashBladeEvent.BreakEvent(stack, cap);
            SlashBladeEvent.BREAK.invoker().onBreak(event);
            cap.setBroken(!event.isCanceled());
        }

        if (current != cap.isBroken()) {
            onBroken.accept(entity);
            if (entity instanceof ServerPlayer player) {
                // stack.getShareTag();
                CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            }

            if (entity instanceof Player player)
                player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
        }

        if (cap.isBroken() && this.isDestructable(stack))
            stack.shrink(1);

        return amount;
    }

    public static Consumer<LivingEntity> getOnBroken(ItemStack stack) {
        return (user) -> {
            user.broadcastBreakEvent(user.getUsedItemHand());

            var state = CapabilitySlashBlade.BLADESTATE.maybeGet(stack).orElseThrow(NullPointerException::new);
            if (stack.isEnchanted()) {
                int count = state.getProudSoulCount() >= SlashBladeConfig.MAX_ENCHANTED_PROUDSOUL_DROP.get() * 100 ?
                        SlashBladeConfig.MAX_ENCHANTED_PROUDSOUL_DROP.get() : Math.max(1, state.getProudSoulCount() / 100);
                List<Enchantment> enchantments = BuiltInRegistries.ENCHANTMENT.stream()
                        .filter(enchantment -> enchantment.canEnchant(stack) /*stack.canApplyAtEnchantingTable(enchantment)*/)
                        .filter(enchantment -> !SlashBladeConfig.NON_DROPPABLE_ENCHANTMENT.get()
                                .contains(BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString()))
                        .toList();
                for (int i = 0; i < count; i += 1) {
                    ItemStack enchanted_soul = new ItemStack(SBItems.proudsoul_tiny);
                    Enchantment enchant = enchantments.get(user.getRandom().nextInt(0, enchantments.size()));
                    if (enchant != null) {
                        enchanted_soul.enchant(enchant, 1);
                        ItemEntity itemEntity = new ItemEntity(user.level(), user.getX(), user.getY(), user.getZ(),
                                enchanted_soul);
                        itemEntity.setDefaultPickUpDelay();
                        user.level().addFreshEntity(itemEntity);
                    }
                    state.setProudSoulCount(state.getProudSoulCount() - 100);
                }
            }
            ItemStack soul = new ItemStack(SBItems.proudsoul_tiny);

            int count = state.getProudSoulCount() >= SlashBladeConfig.MAX_PROUDSOUL_DROP.get() * 100 ?
                    SlashBladeConfig.MAX_PROUDSOUL_DROP.get() : Math.max(1, state.getProudSoulCount() / 100);

            soul.setCount(count);
            state.setProudSoulCount(state.getProudSoulCount() - (count * 100));

            ItemEntity itementity = new ItemEntity(user.level(), user.getX(), user.getY(), user.getZ(), soul);
            BladeItemEntity e = new BladeItemEntity(SBEntityTypes.BladeItem, user.level()) {
                static final String isReleased = "isReleased";

                @Override
                public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource ds) {

                    CompoundTag tag = this.sb$getPersistentData();

                    if (!tag.getBoolean(isReleased)) {
                        this.sb$getPersistentData().putBoolean(isReleased, true);

                        if (this.level() instanceof ServerLevel) {
                            Entity thrower = getOwner();

                            if (thrower != null) {
                                ((EntityExtension) thrower).sb$getPersistentData().remove(BREAK_ACTION_TIMEOUT);
                            }
                        }
                    }

                    return super.causeFallDamage(distance, damageMultiplier, ds);
                }
            };

            e.restoreFrom(itementity);
            e.init();
            e.push(0, 0.4, 0);

            e.setModel(state.getModel().orElse(DefaultResources.resourceDefaultModel));
            e.setTexture(state.getTexture().orElse(DefaultResources.resourceDefaultTexture));

            e.setPickUpDelay(20 * 2);
            e.setGlowingTag(true);

            e.setAirSupply(-1);

            e.setThrower(user.getUUID());

            user.level().addFreshEntity(e);

            ((EntityExtension) user).sb$getPersistentData().putLong(BREAK_ACTION_TIMEOUT, user.level().getGameTime() + 20 * 5);
        };
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent((state) -> {
            ResourceLocation loc = state.resolvCurrentComboState(attacker);
            ComboState cs = ComboStateRegistry.COMBO_STATE.get(loc) != null
                    ? ComboStateRegistry.COMBO_STATE.get(loc)
                    : ComboStateRegistry.NONE;

            SlashBladeEvent.HitEvent event = new SlashBladeEvent.HitEvent(stack, state, target, attacker);
            SlashBladeEvent.HIT.invoker().onHit(event);
            if (event.isCanceled())
                return;

            cs.hitEffect(target, attacker);
            stack.hurtAndBreak(1, attacker, ItemSlashBlade.getOnBroken(stack));

        });

        return true;
    }

    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
                             LivingEntity entityLiving) {

        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent((s) -> {
                stack.hurtAndBreak(1, entityLiving, ItemSlashBlade.getOnBroken(stack));
            });
        }

        return true;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        int elapsed = this.getUseDuration(stack) - timeLeft;

        if (!worldIn.isClientSide()) {

            CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent((state) -> {

                var swordType = SwordType.from(stack);
                if (state.isBroken() || state.isSealed() || !(swordType.contains(SwordType.ENCHANTED)))
                    return;

                ResourceLocation sa = state.doChargeAction(entityLiving, elapsed);

                // sa.tickAction(entityLiving);
                if (!sa.equals(ComboStateRegistry.getId(ComboStateRegistry.NONE))) {

                    var cost = state.getSlashArts().getProudSoulCost();
                    if (state.getProudSoulCount() >= cost)
                        state.setProudSoulCount(state.getProudSoulCount() - cost);
                    else
                        stack.hurtAndBreak(1, entityLiving, ItemSlashBlade.getOnBroken(stack));

                    entityLiving.swing(InteractionHand.MAIN_HAND);
                }
            });
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent((state) -> {

            (ComboStateRegistry.COMBO_STATE.get(state.getComboSeq()) != null
                    ? ComboStateRegistry.COMBO_STATE.get(state.getComboSeq())
                    : ComboStateRegistry.NONE).holdAction(player);
            var swordType = SwordType.from(stack);
            if (state.isBroken() || state.isSealed() || !(swordType.contains(SwordType.ENCHANTED)))
                return;
            if (!player.level().isClientSide()) {
                int ticks = player.getTicksUsingItem();
                int fullChargeTicks = state.getFullChargeTicks(player);
                if (0 < ticks) {
                    if (ticks == fullChargeTicks) {// state.getFullChargeTicks(player)){
                        Vec3 pos = player.getEyePosition(1.0f).add(player.getLookAngle());
                        ((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL, pos.x, pos.y, pos.z, 7, 0.7,
                                0.7, 0.7, 0.02);
                    }
                }
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (stack == null)
            return;
        if (entityIn == null)
            return;

        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent((state) -> {
            SlashBladeEvent.UpdateEvent event = new SlashBladeEvent.UpdateEvent(stack, state, worldIn, entityIn, itemSlot, isSelected);
            SlashBladeEvent.UPDATE.invoker().onUpdate(event);
            if (event.isCanceled())
                return;

            if (!isSelected) {
                var swordType = SwordType.from(stack);
                if (entityIn instanceof Player player) {
                    if (!SlashBladeConfig.SELF_REPAIR_ENABLE.get())
                        return;
                    boolean hasHunger = player.hasEffect(MobEffects.HUNGER) && SlashBladeConfig.HUNGER_CAN_REPAIR.get();
                    if (swordType.contains(SwordType.BEWITCHED) || hasHunger) {
                        if (stack.getDamageValue() > 0 && player.getFoodData().getFoodLevel() > 0) {
                            int hungerAmplifier = hasHunger ? player.getEffect(MobEffects.HUNGER).getAmplifier() : 0;
                            int level = 1 + hungerAmplifier;
                            Boolean expCostFlag = SlashBladeConfig.SELF_REPAIR_COST_EXP.get();
                            int expCost = SlashBladeConfig.BEWITCHED_EXP_COST.get() * level;

                            if (expCostFlag && player.experienceLevel < expCost)
                                return;

                            player.giveExperiencePoints(expCostFlag ? -expCost : 0);
                            player.causeFoodExhaustion(
                                    SlashBladeConfig.BEWITCHED_HUNGER_EXHAUSTION.get().floatValue() * level);
                            stack.setDamageValue(stack.getDamageValue() - level);
                        }
                    }
                }
            }
            if (entityIn instanceof LivingEntity living) {
                CapabilityInputState.INPUT_STATE.maybeGet(entityIn).ifPresent(mInput -> {
                    mInput.getScheduler().onTick(living);
                });

                /*
                 * if(0.5f > state.getDamage()) state.setDamage(0.99f);
                 */
                ResourceLocation loc = state.resolvCurrentComboState(living);
                ComboState cs = ComboStateRegistry.COMBO_STATE.get(loc) != null
                        ? ComboStateRegistry.COMBO_STATE.get(loc)
                        : ComboStateRegistry.NONE;

                if (isInMainhand(stack, isSelected, living))
                    cs.tickAction(living);
                else if (!loc.equals(state.getComboRoot()))
                    state.setComboSeq(state.getComboRoot());
            }
        });
    }

    public static boolean isInMainhand(ItemStack stack, boolean isSelected, LivingEntity living) {
        return isSelected && stack.equals(living.getMainHandItem()/*, false*/);
    }

//    @Nullable
//    @Override
//    public CompoundTag getShareTag(ItemStack stack) {
//        var tag = stack.getOrCreateTag();
//        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(state -> {
//            if (!state.isEmpty())
//                tag.put("bladeState", state.serializeNBT());
//        });
//        return tag;
//    }
//
//    @Override
//    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
//        if (nbt != null) {
//            if (nbt.contains("bladeState"))
//                CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(state -> state.deserializeNBT(nbt.getCompound("bladeState")));
//        }
//        super.readShareTag(stack, nbt);
//    }

    // damage ----------------------------------------------------------

    @Override
    public int getDamage(ItemStack stack) {
        return CapabilitySlashBlade.BLADESTATE.maybeGet(stack).filter(s -> !s.isEmpty()).map(ISlashBladeState::getDamage).orElse(0);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CapabilitySlashBlade.BLADESTATE.maybeGet(stack).filter(s -> !s.isEmpty()).map(ISlashBladeState::getMaxDamage).orElse(this.getTier().getUses());
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return CapabilitySlashBlade.BLADESTATE.maybeGet(stack).filter((s) -> !s.getTranslationKey().isBlank())
                .map(ISlashBladeState::getTranslationKey).orElseGet(() -> stackDefaultDescriptionId(stack));
    }

    private String stackDefaultDescriptionId(ItemStack stack) {
        var cap = CapabilitySlashBlade.BLADESTATE.maybeGet(stack);
        if (cap.isEmpty())
            return super.getDescriptionId(stack);
        String key = cap.get().getTranslationKey();
        return !key.isBlank() ? key : super.getDescriptionId(stack);
    }

    public boolean isDestructable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {

        if (Ingredient.of(ItemTags.STONE_TOOL_MATERIALS).test(repair)) {
            return true;
        }

        /*
         * Tag<Item> tags = ItemTags.getCollection().get(new
         * ResourceLocation("slashblade","proudsouls"));
         *
         * if(tags != null){ boolean result = Ingredient.fromTag(tags).test(repair); }
         */

        // todo: repair custom material
        if (repair.is(SlashBladeItemTags.PROUD_SOULS))
            return true;
        return super.isValidRepairItem(toRepair, repair);
    }

    @SuppressWarnings("UnstableApiUsage")
    RangeMap<Comparable<?>, Object> refineColor = ImmutableRangeMap.builder()
            .put(Range.lessThan(10), ChatFormatting.GRAY).put(Range.closedOpen(10, 50), ChatFormatting.YELLOW)
            .put(Range.closedOpen(50, 100), ChatFormatting.GREEN).put(Range.closedOpen(100, 150), ChatFormatting.AQUA)
            .put(Range.closedOpen(150, 200), ChatFormatting.BLUE).put(Range.atLeast(200), ChatFormatting.LIGHT_PURPLE)
            .build();

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        CapabilitySlashBlade.BLADESTATE.maybeGet(stack).ifPresent(s -> {
            this.appendSwordType(stack, worldIn, tooltip, flagIn); // √
            this.appendProudSoulCount(tooltip, stack, s);
            this.appendKillCount(tooltip, stack, s);
            this.appendSlashArt(stack, tooltip, s); // √
            this.appendRefineCount(tooltip, stack, s);
            this.appendSpecialEffects(tooltip, s); // √
        });

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Environment(EnvType.CLIENT)
    public void appendSlashArt(ItemStack stack, List<Component> tooltip, @NotNull ISlashBladeState s) {
        var swordType = SwordType.from(stack);
        if (swordType.contains(SwordType.BEWITCHED) && !swordType.contains(SwordType.SEALED)) {
            tooltip.add(Component.translatable("slashblade.tooltip.slash_art", s.getSlashArts().getDescription())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendRefineCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
        int refine = s.getRefine();
        if (refine > 0) {
            tooltip.add(Component.translatable("slashblade.tooltip.refine", refine)
                    .withStyle((ChatFormatting) refineColor.get(refine)));
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendProudSoulCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
        int proudsoul = s.getProudSoulCount();
        if (proudsoul > 0) {
            MutableComponent countComponent = Component.translatable("slashblade.tooltip.proud_soul", proudsoul)
                    .withStyle(ChatFormatting.GRAY);
            if (proudsoul > 1000)
                countComponent = countComponent.withStyle(ChatFormatting.DARK_PURPLE);
            tooltip.add(countComponent);
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendKillCount(List<Component> tooltip, @NotNull ItemStack stack, @NotNull ISlashBladeState s) {
        int killCount = s.getKillCount();
        if (killCount > 0) {
            MutableComponent killCountComponent = Component.translatable("slashblade.tooltip.killcount", killCount)
                    .withStyle(ChatFormatting.GRAY);
            if (killCount > 1000)
                killCountComponent = killCountComponent.withStyle(ChatFormatting.DARK_PURPLE);
            tooltip.add(killCountComponent);
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendSpecialEffects(List<Component> tooltip, @NotNull ISlashBladeState s) {
        if (s.getSpecialEffects().isEmpty())
            return;

        Minecraft mcinstance = Minecraft.getInstance();
        Player player = mcinstance.player;

        s.getSpecialEffects().forEach(se -> {

            boolean showingLevel = SpecialEffect.getRequestLevel(se) > 0;

            tooltip.add(Component.translatable("slashblade.tooltip.special_effect", SpecialEffect.getDescription(se),
                            Component.literal(showingLevel ? String.valueOf(SpecialEffect.getRequestLevel(se)) : "")
                                    .withStyle(SpecialEffect.isEffective(se, player.experienceLevel) ? ChatFormatting.RED
                                            : ChatFormatting.DARK_GRAY))
                    .withStyle(ChatFormatting.GRAY));
        });
    }

    @Environment(EnvType.CLIENT)
    public void appendSwordType(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        var swordType = SwordType.from(stack);
        if (swordType.contains(SwordType.SEALED)) return;
        if (swordType.contains(SwordType.BEWITCHED)) {
            tooltip.add(
                    Component.translatable("slashblade.sword_type.bewitched").withStyle(ChatFormatting.DARK_PURPLE));
        } else if (swordType.contains(SwordType.ENCHANTED)) {
            tooltip.add(Component.translatable("slashblade.sword_type.enchanted").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(Component.translatable("slashblade.sword_type.noname").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /**
     * @return true = cancel : false = swing
     */
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return CapabilitySlashBlade.BLADESTATE.maybeGet(stack).filter(s -> s.getLastActionTime() == entity.level().getGameTime())
                .isEmpty();
    }

/*    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }*/

    /**
     * 原来的方法替换掉落实体时无法Copy假物品实体相关的NBT，因为获取物品指令是先生成的物品实体再设置的假物品
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!(entity instanceof BladeItemEntity)) {
            Level world = entity.level();
            BladeItemEntity e = new BladeItemEntity(SBEntityTypes.BladeItem, world);
            e.restoreFrom(entity);
            e.init();
            entity.discard();
            world.addFreshEntity(e);
        }
        return false;
    }

/*    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return super.getEntityLifespan(itemStack, world);// Short.MAX_VALUE;
    }*/

    @Environment(EnvType.CLIENT)
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new SlashBladeTEISR(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }
}
