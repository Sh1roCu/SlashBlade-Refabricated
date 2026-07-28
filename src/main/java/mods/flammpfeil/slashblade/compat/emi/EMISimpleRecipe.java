//package mods.flammpfeil.slashblade.compat.emi;
//
//import dev.emi.emi.api.recipe.EmiRecipe;
//import dev.emi.emi.api.stack.EmiIngredient;
//import dev.emi.emi.api.stack.EmiStack;
//import net.minecraft.resources.Identifier;
//
//import java.util.List;
//
//public abstract class EMISimpleRecipe implements EmiRecipe {
//    protected final List<EmiIngredient> inputs;
//    protected final List<EmiStack> outputs;
//    protected final Identifier id;
//
//    public EMISimpleRecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, Identifier id) {
//        this.inputs = inputs;
//        this.outputs = outputs;
//        this.id = id;
//    }
//
//    @Override
//    public List<EmiIngredient> getInputs() {
//        return inputs;
//    }
//
//    @Override
//    public List<EmiStack> getOutputs() {
//        return outputs;
//    }
//
//    @Override
//    public Identifier getId() {
//        return id;
//    }
//}