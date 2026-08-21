//package mods.flammpfeil.slashblade.emi.mixin;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonPrimitive;
//import com.mojang.serialization.DynamicOps;
//import dev.emi.emi.EmiPort;
//import dev.emi.emi.api.stack.EmiIngredient;
//import dev.emi.emi.api.stack.EmiStack;
//import dev.emi.emi.api.stack.ItemEmiStack;
//import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
//import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
//import dev.emi.emi.runtime.EmiLog;
//import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
//import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
//import mods.flammpfeil.slashblade.item.ItemSlashBlade;
//import net.minecraft.client.Minecraft;
//import net.minecraft.core.Holder;
//import net.minecraft.core.component.DataComponentPatch;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.NbtOps;
//import net.minecraft.nbt.Tag;
//import net.minecraft.nbt.TagParser;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//
//import java.util.regex.Matcher;
//
//@Mixin(value = ItemEmiStackSerializer.class, remap = false)
//public abstract class MixinItemEmiStackSerializer implements EmiStackSerializer<ItemEmiStack> {
//
//    private static <T> DynamicOps<T> withRegistryAccess(DynamicOps<T> ops) {
//        Minecraft instance = Minecraft.getInstance();
//        return instance != null && instance.level != null ? instance.level.registryAccess().createSerializationContext(ops) : ops;
//    }
//
//    @Override
//    public JsonElement serialize(ItemEmiStack stack) {
//        if (stack.getAmount() == 1 && stack.getChance() == 1 &&
//                stack.getRemainder().isEmpty() &&
//                !(stack.getItemStack().getItem() instanceof ItemSlashBlade)) {
//            String s = getType() + ":" + stack.getId();
//            DataComponentPatch patch = stack.getComponentChanges();
//            if (!patch.isEmpty()) {
//                s += DataComponentPatch.CODEC.encodeStart(withRegistryAccess(NbtOps.INSTANCE), patch).getOrThrow().getAsString();
//            }
//            return new JsonPrimitive(s);
//
//        } else {
//            JsonObject json = new JsonObject();
//            json.addProperty("type", getType());
//            json.addProperty("id", stack.getId().toString());
//            DataComponentPatch patch = stack.getComponentChanges();
//            if (!patch.isEmpty()) {
//                json.addProperty("nbt", DataComponentPatch.CODEC.encodeStart(withRegistryAccess(NbtOps.INSTANCE), patch).getOrThrow().getAsString());
//            }
//            if (stack.getAmount() != 1) {
//                json.addProperty("amount", stack.getAmount());
//            }
//            if (stack.getChance() != 1) {
//                json.addProperty("chance", stack.getChance());
//            }
//            ItemStack itemStack = stack.getItemStack();
//            if (itemStack.getItem() instanceof ItemSlashBlade) {
//                var optional = CapabilitySlashBlade.getBladeState(itemStack);
//                if (optional.isPresent()) {
//                    json.addProperty("sbCaps", optional.orElseThrow().getBladeState().getAsString());
//
//                }
//            }
//            if (!stack.getRemainder().isEmpty()) {
//                EmiStack remainder = stack.getRemainder();
//                if (!remainder.getRemainder().isEmpty()) {
//                    remainder = remainder.copy().setRemainder(EmiStack.EMPTY);
//                }
//                if (remainder.getRemainder().isEmpty()) {
//                    JsonElement remainderElement = EmiIngredientSerializer.getSerialized(remainder);
//                    if (remainderElement != null) {
//                        json.add("remainder", remainderElement);
//                    }
//                }
//            }
//            return json;
//        }
//    }
//
//    @Override
//    public EmiIngredient deserialize(JsonElement element) {
//        ResourceLocation id = null;
//        String nbt = null;
//        String capNBT = null;
//        long amount = 1;
//        float chance = 1;
//        EmiStack remainder = EmiStack.EMPTY;
//        if (GsonHelper.isStringValue(element)) {
//            String s = element.getAsString();
//            Matcher m = STACK_REGEX.matcher(s);
//            if (m.matches()) {
//                id = EmiPort.id(m.group(2), m.group(3));
//                nbt = m.group(4);
//            }
//        } else if (element.isJsonObject()) {
//            JsonObject json = element.getAsJsonObject();
//            id = EmiPort.id(GsonHelper.getAsString(json, "id"));
//            nbt = GsonHelper.getAsString(json, "nbt", null);
//            capNBT = GsonHelper.getAsString(json, "sbCaps", null);
//            amount = GsonHelper.getAsLong(json, "amount", 1);
//            chance = GsonHelper.getAsFloat(json, "chance", 1);
//            if (GsonHelper.isValidNode(json, "remainder")) {
//                EmiIngredient ing = EmiIngredientSerializer.getDeserialized(json.get("remainder"));
//                if (ing instanceof EmiStack stack) {
//                    remainder = stack;
//                }
//            }
//        }
//        if (id != null) {
//            try {
//                DataComponentPatch nbtPatch = DataComponentPatch.EMPTY;
//                if (nbt != null) {
//                    Tag tag = TagParser.parseTag(nbt);
//                    nbtPatch = DataComponentPatch.CODEC.parse(withRegistryAccess(NbtOps.INSTANCE), tag).getOrThrow();
//                }
//                EmiStack stack;
//                if (capNBT != null) {
//                    var holderOpt = EmiPort.getItemRegistry().getHolder(id);
//                    if (holderOpt.isEmpty()) {
//                        return EmiStack.EMPTY;
//                    }
//                    Holder<Item> holder = holderOpt.get();
//                    CompoundTag capTag = TagParser.parseTag(capNBT);
//                    ItemStack itemStack = new ItemStack(holder, (int) amount, nbtPatch);
//                    CapabilitySlashBlade.getBladeState(itemStack).ifPresent(state -> state.setBladeState(capTag));
//                    EmiStack emiStack = EmiStack.of(itemStack);
//                    if (chance != 1) {
//                        emiStack.setChance(chance);
//                    }
//                    if (!remainder.isEmpty()) {
//                        emiStack.setRemainder(remainder);
//                    }
//                    stack = emiStack;
//                } else {
//                    stack = create(id, nbtPatch, amount);
//                }
//                if (chance != 1) {
//                    stack.setChance(chance);
//                }
//                if (!remainder.isEmpty()) {
//                    stack.setRemainder(remainder);
//                }
//
//                return stack;
//            } catch (Exception e) {
//                EmiLog.error("Error parsing NBT in deserialized stack", e);
//                return EmiStack.EMPTY;
//            }
//        }
//        return EmiStack.EMPTY;
//    }
//}