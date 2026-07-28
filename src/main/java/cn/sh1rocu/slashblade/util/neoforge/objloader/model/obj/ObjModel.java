/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package cn.sh1rocu.slashblade.util.neoforge.objloader.model.obj;

import cn.sh1rocu.slashblade.util.neoforge.objloader.model.AbstractUnbakedModel;
import cn.sh1rocu.slashblade.util.neoforge.objloader.model.StandardModelParameters;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.resources.Identifier;

/**
 * A model loaded from an OBJ file.
 * <p>
 * Supports positions, texture coordinates, normals and colors. The {@link ObjMaterialLibrary material library}
 * has support for numerous features, including support for {@link Identifier} textures (non-standard).
 */
public class ObjModel extends AbstractUnbakedModel {
    private final ObjGeometry geometry;

    public ObjModel(StandardModelParameters parameters, ObjGeometry geometry) {
        super(parameters);
        this.geometry = geometry;
    }

    @Override
    public UnbakedGeometry geometry() {
        return geometry;
    }
}
