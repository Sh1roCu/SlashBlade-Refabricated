/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package cn.sh1rocu.slashblade.util.neoforge.objloader.model;

import cn.sh1rocu.slashblade.util.neoforge.objloader.extensions.UnbakedModelExtension;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;

/**
 * Base interface for unbaked models that wish to support the NeoForge-added {@code bake} method
 * that receives {@linkplain UnbakedModelExtension#fillAdditionalProperties(ContextMap.Builder) additional properties}.
 */
@FunctionalInterface
public interface ExtendedUnbakedGeometry extends UnbakedGeometry {
    ContextKeySet SET_EMPTY = (new ContextKeySet.Builder()).build();
    ContextMap MAP_EMPTY = new ContextMap.Builder().create(SET_EMPTY);

    @Override
    default QuadCollection bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, ModelDebugName name) {
        return bake(textureSlots, modelBaker, modelState, name, MAP_EMPTY);
    }

    // Re-abstract the extended version
    QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties);
}
