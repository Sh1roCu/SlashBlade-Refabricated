package mods.flammpfeil.slashblade.emi.mixin;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemEmiStackSerializer.class, remap = false)
public abstract class MixinItemEmiStackSerializer implements EmiStackSerializer<EmiStack> {
//    private static <T> DynamicOps<T> withRegistryAccess(DynamicOps<T> ops) {
//        Minecraft instance = Minecraft.getInstance();
//        return instance != null && instance.level != null ? instance.level.registryAccess().createSerializationContext(ops) : ops;
//    }
//
//    @Override
//    public EmiIngredient deserialize(JsonElement element) {
//        ResourceLocation id = null;
//        String nbt = null;
//        String sbCap = null;
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
//            sbCap = GsonHelper.getAsString(json, "sbCap", null);
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
//                DataComponentPatch changes = DataComponentPatch.EMPTY;
//                if (nbt != null) {
//                    CompoundTag tag;
//                    if (sbCap != null)
//                        tag = TagParser.parseTag(nbt).merge(TagParser.parseTag(sbCap));
//                    else
//                        tag = TagParser.parseTag(nbt);
//
//                    changes = (DataComponentPatch) ((Pair<?, ?>) DataComponentPatch.CODEC.decode(withRegistryAccess(NbtOps.INSTANCE), tag).getOrThrow()).getFirst();
//                }
//                EmiStack stack = this.create(id, changes, amount);
//                if (chance != 1.0F) {
//                    stack.setChance(chance);
//                }
//
//                if (!remainder.isEmpty()) {
//                    stack.setRemainder(remainder);
//                }
//
//                return stack;
//            } catch (Exception e) {
//                EmiLog.error("Error parsing NBT in deserialized stack", e);
//                return EmiStack.EMPTY;
//            }
//        } else {
//            return EmiStack.EMPTY;
//        }
//    }
//
//    @Override
//    public JsonElement serialize(EmiStack stack) {
//        String nbt = null;
//        String sbCap = null;
//        var itemStack = stack.getItemStack();
//        DataComponentPatch componentChanges = stack.getComponentChanges();
//        if (componentChanges != DataComponentPatch.EMPTY) {
//            nbt = (DataComponentPatch.CODEC.encodeStart(withRegistryAccess(NbtOps.INSTANCE), componentChanges).getOrThrow().getAsString());
//        }
//
//        var cap = CapabilitySlashBlade.getBladeState(itemStack);
//        if (cap.isPresent()) {
//            sbCap = cap.get().getBladeState().getAsString();
//        }
//
//        if (stack.getAmount() == 1L && stack.getChance() == 1.0F && stack.getRemainder().isEmpty() && sbCap == null) {
//            String var10000 = this.getType();
//            String s = var10000 + ":" + stack.getId();
//            if (nbt != null) {
//                s = s + nbt;
//            }
//
//            return new JsonPrimitive(s);
//        } else {
//            JsonObject json = new JsonObject();
//            json.addProperty("type", getType());
//            json.addProperty("id", stack.getId().toString());
//            if (nbt != null) {
//                json.addProperty("nbt", nbt);
//            }
//            if (sbCap != null) {
//                json.addProperty("sbCap", sbCap);
//            }
//            if (stack.getAmount() != 1) {
//                json.addProperty("amount", stack.getAmount());
//            }
//            if (stack.getChance() != 1) {
//                json.addProperty("chance", stack.getChance());
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
}