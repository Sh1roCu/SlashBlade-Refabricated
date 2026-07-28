package mods.flammpfeil.slashblade.entity;

import cn.sh1rocu.slashblade.util.network.IEntityExtension;
import cn.sh1rocu.slashblade.util.network.IEntityWithComplexSpawn;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.init.SBEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BladeStandEntity extends ItemFrame implements IEntityWithComplexSpawn, IEntityExtension {

    public Item currentType = null;
    public ItemStack currentTypeStack = ItemStack.EMPTY;

    public BladeStandEntity(EntityType<? extends BladeStandEntity> p_i50224_1_, Level p_i50224_2_) {
        super(p_i50224_1_, p_i50224_2_);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        String standTypeStr;
        if (this.currentType != null) {
            standTypeStr = BuiltInRegistries.ITEM.getKey(this.currentType).toString();
        } else {
            standTypeStr = "";
        }
        output.putString("StandType", standTypeStr);

        output.putByte("Pose", (byte) this.getPose().ordinal());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.currentType = BuiltInRegistries.ITEM.getValue(Identifier.parse(input.getStringOr("StandType", "")));

        this.setPose(Pose.values()[input.getByteOr("Pose", (byte) 0) % Pose.values().length]);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.registryAccess());
        this.addAdditionalSaveData(output);
        buffer.writeNbt(output.buildResult());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        CompoundTag tag = additionalData.readNbt();
        if (tag != null) {
            this.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, this.registryAccess(), tag));

        }
    }

    public static BladeStandEntity createInstanceFromPos(Level worldIn, BlockPos placePos, Direction dir, Item type) {
        BladeStandEntity e = new BladeStandEntity(SBEntityTypes.BLADE_STAND, worldIn);

        e.pos = placePos;
        e.setDirection(dir);
        e.currentType = type;

        return e;
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ServerLevel level, ItemLike iip) {
        if (iip == Items.ITEM_FRAME) {
            if (this.currentType == null || this.currentType == Items.AIR)
                return null;

            iip = this.currentType;
        }
        return super.spawnAtLocation(level, iip);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float cat) {
        ItemStack blade = this.getItem();

        if (blade.isEmpty())
            return super.hurtServer(level, damageSource, cat);

        if (CapabilitySlashBlade.getBladeState(blade).isEmpty())
            return super.hurtServer(level, damageSource, cat);

        ISlashBladeState state = CapabilitySlashBlade.getBladeState(blade).orElseThrow(NullPointerException::new);

        SlashBladeEvent.BladeStandAttackEvent event = new SlashBladeEvent.BladeStandAttackEvent(blade, state, this, damageSource);
        SlashBladeEvent.BLADE_STAND_ATTACK.invoker().onBladeStandAttack(event);
        if (event.isCanceled()) {
            return true;
        }

        return super.hurtServer(level, damageSource, cat);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 position) {
        InteractionResult result = InteractionResult.PASS;
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (player.isShiftKeyDown() && !this.getItem().isEmpty()) {
                Pose current = this.getPose();
                int newIndex = (current.ordinal() + 1) % Pose.values().length;
                this.setPose(Pose.values()[newIndex]);
                result = InteractionResult.SUCCESS;
            } else if ((!itemstack.isEmpty() && CapabilitySlashBlade.getBladeState(itemstack).isPresent())
                    || (itemstack.isEmpty() && !this.getItem().isEmpty())) {

                if (this.getItem().isEmpty()) {
                    if (!this.isRemoved()) {
                        this.setItem(itemstack);
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
                        result = InteractionResult.SUCCESS;
                    }
                } else {
                    ItemStack displayed = this.getItem().copy();

                    this.setItem(itemstack);
                    player.setItemInHand(hand, displayed);

                    this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                    result = InteractionResult.SUCCESS;

                }

            } else {
                this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
                result = InteractionResult.SUCCESS;
            }
        }
        return result;
    }

    @Override
    protected ItemStack getFrameItemStack() {
        return new ItemStack(currentType);
    }

    @Override
    public boolean survives() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        ItemStack blade = this.getItem();
        if (blade.isEmpty()) return;
        ISlashBladeState state = CapabilitySlashBlade.getBladeState(blade).orElseThrow(NullPointerException::new);
        SlashBladeEvent.BLADE_STAND_TICK.invoker().onBladeStandTick(new SlashBladeEvent.BladeStandTickEvent(blade, state, this));
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        double d0 = 2D / 16D;
        Vec3 vec3 = Vec3.atCenterOf(blockPos).relative(direction, -d0);
        double d = 0.75;
        double e = 0.75;
        double g = 0.75;
        return AABB.ofSize(vec3, d, e, g);
    }
}
