package mods.flammpfeil.slashblade.emi.mixin;

import cn.sh1rocu.slashblade.mixin.accessor.ItemStackAccessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;
import java.util.regex.Matcher;

@Mixin(value = ItemEmiStackSerializer.class, remap = false)
public abstract class MixinItemEmiStackSerializer implements EmiStackSerializer<ItemEmiStack> {

    @Override
    public JsonElement serialize(ItemEmiStack stack) {
        if (stack.getAmount() == 1 && stack.getChance() == 1 &&
                stack.getRemainder().isEmpty() &&
                !(stack.getItemStack().getItem() instanceof ItemSlashBlade)) {
            String s = getType() + ":" + stack.getId();
            if (stack.hasNbt()) {
                s += stack.getNbt().getAsString();
            }
            return new JsonPrimitive(s);

        } else {
            JsonObject json = new JsonObject();
            json.addProperty("type", getType());
            json.addProperty("id", stack.getId().toString());
            if (stack.hasNbt()) {
                json.addProperty("nbt", stack.getNbt().getAsString());
            }
            if (stack.getAmount() != 1) {
                json.addProperty("amount", stack.getAmount());
            }
            if (stack.getChance() != 1) {
                json.addProperty("chance", stack.getChance());
            }
            ItemStack itemStack = stack.getItemStack();
            if (itemStack.getItem() instanceof ItemSlashBlade) {
                var optional = CapabilitySlashBlade.BLADESTATE.maybeGet(itemStack);
                if (optional.isPresent()) {
                    json.addProperty("sbCaps", optional.orElseThrow(NullPointerException::new).getBladeState().getAsString());

                }
            }
            if (!stack.getRemainder().isEmpty()) {
                EmiStack remainder = stack.getRemainder();
                if (!remainder.getRemainder().isEmpty()) {
                    remainder = remainder.copy().setRemainder(EmiStack.EMPTY);
                }
                if (remainder.getRemainder().isEmpty()) {
                    JsonElement remainderElement = EmiIngredientSerializer.getSerialized(remainder);
                    if (remainderElement != null) {
                        json.add("remainder", remainderElement);
                    }
                }
            }
            return json;
        }
    }

    @Override
    public EmiIngredient deserialize(JsonElement element) {
        ResourceLocation id = null;
        String nbt = null;
        String capNBT = null;
        long amount = 1;
        float chance = 1;
        EmiStack remainder = EmiStack.EMPTY;
        if (GsonHelper.isStringValue(element)) {
            String s = element.getAsString();
            Matcher m = STACK_REGEX.matcher(s);
            if (m.matches()) {
                id = EmiPort.id(m.group(2), m.group(3));
                nbt = m.group(4);
            }
        } else if (element.isJsonObject()) {
            JsonObject json = element.getAsJsonObject();
            id = EmiPort.id(GsonHelper.getAsString(json, "id"));
            nbt = GsonHelper.getAsString(json, "nbt", null);
            capNBT = GsonHelper.getAsString(json, "sbCaps");
            amount = GsonHelper.getAsLong(json, "amount", 1);
            chance = GsonHelper.getAsFloat(json, "chance", 1);
            if (GsonHelper.isValidNode(json, "remainder")) {
                EmiIngredient ing = EmiIngredientSerializer.getDeserialized(json.get("remainder"));
                if (ing instanceof EmiStack stack) {
                    remainder = stack;
                }
            }
        }
        if (id != null) {
            try {
                CompoundTag nbtComp = null;
                if (nbt != null) {
                    nbtComp = TagParser.parseTag(nbt);
                }
                EmiStack stack;
                if (capNBT != null) {
                    CompoundTag tag = new CompoundTag();
                    tag.put("Parent", TagParser.parseTag(capNBT));
                    ItemStack itemStack = ItemStackAccessor.sb$create(
                            EmiPort.getItemRegistry().get(id),
                            (int) amount,
                            Optional.of(tag)
                    );
                    CompoundTag newNbt = itemStack.getOrCreateTag();
                    if (nbtComp != null) {
                        for (String key : nbtComp.getAllKeys()) {
                            newNbt.put(key, nbtComp.get(key).copy());
                        }
                    }

                    stack = new ItemEmiStack(itemStack);
                } else {
                    stack = create(id, nbtComp, amount);
                }
                if (chance != 1) {
                    stack.setChance(chance);
                }
                if (!remainder.isEmpty()) {
                    stack.setRemainder(remainder);
                }

                return stack;
            } catch (Exception e) {
                EmiLog.error("Error parsing NBT in deserialized stack", e);
                return EmiStack.EMPTY;
            }
        }
        return EmiStack.EMPTY;
    }
}