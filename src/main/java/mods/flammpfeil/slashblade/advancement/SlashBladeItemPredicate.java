package mods.flammpfeil.slashblade.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlashBladeItemPredicate extends ItemPredicate {
    private final RequestDefinition request;

    public SlashBladeItemPredicate(JsonObject json) {
        this(json.getAsJsonObject("requestBlade").isJsonNull()
                ? RequestDefinition.Builder.newInstance().build()
                : RequestDefinition.fromJSON(json.getAsJsonObject("requestBlade")));
    }

    @Override
    public @NotNull JsonElement serializeToJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", SlashBlade.prefix("slashblade").toString());
        jsonobject.add("requestBlade", this.getRequest().toJson());
        return jsonobject;
    }

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