package cn.sh1rocu.slashblade.util;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Porting Lib
public class ItemPredicateRegistry {
    public static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> custom_predicates = new HashMap<>();
    private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> unmod_predicates = Collections.unmodifiableMap(custom_predicates);

    public static void register(ResourceLocation name, Function<JsonObject, ItemPredicate> deserializer) {
        custom_predicates.put(name, deserializer);
    }

    public static Map<ResourceLocation, Function<JsonObject, ItemPredicate>> getPredicates() {
        return unmod_predicates;
    }
}