package mods.flammpfeil.slashblade.registry.slashblade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.init.DefaultResources;
import net.minecraft.resources.Identifier;

public class RenderDefinition {
    public static final Codec<RenderDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("texture", DefaultResources.resourceDefaultTexture)
                    .forGetter(RenderDefinition::getTextureName),
            Identifier.CODEC.optionalFieldOf("model", DefaultResources.resourceDefaultModel)
                    .forGetter(RenderDefinition::getModelName),
            Codec.INT.optionalFieldOf("summon_sword_color", 0xFF3333FF)
                    .forGetter(RenderDefinition::getSummonedSwordColor),
            Codec.BOOL.optionalFieldOf("color_inverse", false).forGetter(RenderDefinition::isSummonedSwordColorInverse),
            CarryType.CODEC.optionalFieldOf("carry_type", CarryType.PSO2)
                    .forGetter(RenderDefinition::getStandbyRenderType))
            .apply(instance, RenderDefinition::new));

    private final Identifier TextureName;
    private final Identifier ModelName;
    private final int SummonedSwordColor;
    private final boolean SummonedSwordColorInverse;
    private final CarryType StandbyRenderType;

    private RenderDefinition(Identifier texture, Identifier model, int color, boolean colorInverse,
            CarryType standby) {
        this.TextureName = texture;
        this.ModelName = model;
        this.SummonedSwordColor = color;
        this.SummonedSwordColorInverse = colorInverse;
        this.StandbyRenderType = standby;
    }

    public Identifier getModelName() {
        return ModelName;
    }

    public Identifier getTextureName() {
        return TextureName;
    }

    public boolean isSummonedSwordColorInverse() {
        return SummonedSwordColorInverse;
    }

    public int getSummonedSwordColor() {
        return SummonedSwordColor;
    }

    public CarryType getStandbyRenderType() {
        return StandbyRenderType;
    }

    public static class Builder {
        private Identifier TextureName;
        private Identifier ModelName;
        private int SummonedSwordColor;
        private boolean SummonedSwordColorInverse;
        private CarryType StandbyRenderType;

        private Builder() {
            this.TextureName = DefaultResources.resourceDefaultTexture;
            this.ModelName = DefaultResources.resourceDefaultModel;
            this.SummonedSwordColor = 0XFF3333FF;
            this.SummonedSwordColorInverse = false;
            this.StandbyRenderType = CarryType.DEFAULT;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder textureName(Identifier TextureName) {
            this.TextureName = TextureName;
            return this;
        }

        public Builder modelName(Identifier ModelName) {
            this.ModelName = ModelName;
            return this;
        }

        public Builder effectColor(int SummonedSwordColor) {
            this.SummonedSwordColor = SummonedSwordColor;
            return this;
        }

        public Builder effectColorInverse(boolean SummonedSwordColorInverse) {
            this.SummonedSwordColorInverse = SummonedSwordColorInverse;
            return this;
        }

        public Builder standbyRenderType(CarryType standbyRenderType) {
            this.StandbyRenderType = standbyRenderType;
            return this;
        }

        public RenderDefinition build() {
            return new RenderDefinition(TextureName, ModelName, SummonedSwordColor, SummonedSwordColorInverse,
                    StandbyRenderType);
        }
    }
}