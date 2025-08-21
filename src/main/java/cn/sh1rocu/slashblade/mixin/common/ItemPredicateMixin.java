package cn.sh1rocu.slashblade.mixin.common;

import cn.sh1rocu.slashblade.util.ItemPredicateRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// Porting Lib
@Mixin(ItemPredicate.class)
public abstract class ItemPredicateMixin {
    @Inject(method = "fromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void sb$customItemPredicates(JsonElement json, CallbackInfoReturnable<ItemPredicate> cir, JsonObject jsonobject) {
        if (jsonobject.has("type")) {
            final ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(jsonobject, "type"));
            if (ItemPredicateRegistry.custom_predicates.containsKey(rl))
                cir.setReturnValue(ItemPredicateRegistry.custom_predicates.get(rl).apply(jsonobject));
            else throw new JsonSyntaxException("There is no ItemPredicate of type " + rl);
        }
    }
}