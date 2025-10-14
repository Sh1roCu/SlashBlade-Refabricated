package mods.flammpfeil.slashblade.emi.mixin;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemEmiStackSerializer.class, remap = false)
public abstract class MixinItemEmiStackSerializer implements EmiStackSerializer<EmiStack> {
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
//            capNBT = GsonHelper.getAsString(json, "sbCaps");
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
//                CompoundTag nbtComp = null;
//                DataComponentPatch changes = DataComponentPatch.EMPTY;
//                if (nbt != null) {
//                    nbtComp = TagParser.parseTag(nbt);
//                    changes = DataComponentPatch.CODEC.decode(Minecraft.getInstance().level.registryAccess().createSerializationContext(NbtOps.INSTANCE), nbtComp).getOrThrow().getFirst();\
//                }
//                EmiStack stack = create(id, changes, amount);
//                if (chance != 1) {
//                    stack.setChance(chance);
//                }
//                if (!remainder.isEmpty()) {
//                    stack.setRemainder(remainder);
//                }
//                return stack;
//            } catch (Exception e) {
//                EmiLog.error("Error parsing NBT in deserialized stack");
//                e.printStackTrace();
//                return EmiStack.EMPTY;
//            }
//        }
//        return EmiStack.EMPTY;
//    }
//
//    @Override
//    public JsonElement serialize(EmiStack stack) {
//        if (stack.getAmount() == 1 && stack.getChance() == 1 && stack.getRemainder().isEmpty()
//                && !(stack.getItemStack().getItem() instanceof ItemSlashBlade)) {
//            String s = getType() + ":" + stack.getId();
//            var componentChanges = stack.getComponentChanges();
//            if (componentChanges != DataComponentPatch.EMPTY) {
//                s += DataComponentPatch.CODEC.encodeStart(Minecraft.getInstance().level.registryAccess().createSerializationContext(NbtOps.INSTANCE), componentChanges).getOrThrow().getAsString();
//            }
//            return new JsonPrimitive(s);
//        } else {
//            JsonObject json = new JsonObject();
//            json.addProperty("type", getType());
//            json.addProperty("id", stack.getId().toString());
//            var data = stack.get(DataComponents.CUSTOM_DATA);
//            if (data != null) {
//                json.addProperty("nbt", data.copyTag().getAsString());
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
//                    json.addProperty("sbCaps", optional.orElseThrow(NullPointerException::new).getBladeState().getAsString());
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
}