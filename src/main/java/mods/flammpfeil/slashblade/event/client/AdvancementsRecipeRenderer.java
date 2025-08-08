package mods.flammpfeil.slashblade.event.client;

import cn.sh1rocu.slashblade.mixin.accessor.AdvancementTabAccessor;
import cn.sh1rocu.slashblade.mixin.accessor.AdvancementWidgetAccessor;
import cn.sh1rocu.slashblade.mixin.accessor.AdvancementsScreenAccessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import mods.flammpfeil.slashblade.SlashBlade;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class AdvancementsRecipeRenderer implements PlaceRecipe<Ingredient> {

    private static final Minecraft MCINSTANCE = Minecraft.getInstance();
    private static final ResourceLocation GUI_TEXTURE_CRAFTING_TABLE = new ResourceLocation(
            "textures/gui/container/crafting_table.png");
    private static final ResourceLocation GUI_TEXTURE_FURNACE = new ResourceLocation(
            "textures/gui/container/furnace.png");
    private static final ResourceLocation GUI_TEXTURE_BLAST_FURNACE = new ResourceLocation(
            "textures/gui/container/blast_furnace.png");
    private static final ResourceLocation GUI_TEXTURE_SMOKER = new ResourceLocation(
            "textures/gui/container/smoker.png");
    private static final ResourceLocation GUI_TEXTURE_SMITHING = new ResourceLocation(
            "textures/gui/container/smithing.png");
    private static final ResourceLocation GUI_TEXTURE_ANVIL = new ResourceLocation("textures/gui/container/anvil.png");

    private static final class SingletonHolder {
        private static final AdvancementsRecipeRenderer instance = new AdvancementsRecipeRenderer();
    }

    public static AdvancementsRecipeRenderer getInstance() {
        return SingletonHolder.instance;
    }

    private AdvancementsRecipeRenderer() {
    }

    public void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            this.onInitGuiPost(client, screen, scaledWidth, scaledHeight);
            ScreenEvents.afterRender(screen).register(this::onDrawScreenPost);
        });
    }


    public static GhostRecipe gr = new GhostRecipe();

    public static ResourceLocation currentRecipe = null;

    static final RecipeType<DummyAnvilRecipe> dummy_anvilType = new RecipeType<DummyAnvilRecipe>() {
        public String toString() {
            return "sb_forgeing";
        }
    };
    /* RecipeType.register("sb_forgeing"); */

    static RecipeView currentView = null;
    static Map<RecipeType<?>, RecipeView> typeRecipeViewMap = createRecipeViewMap();

    static class DummyAnvilRecipe implements Recipe<Container> {
        protected SmithingTransformRecipe original;
        private final ItemStack result;
        private final ResourceLocation recipeId;

        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(2, Ingredient.EMPTY);

        public DummyAnvilRecipe(SmithingTransformRecipe recipe) {
            original = recipe;

            FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer());
            SmithingTransformRecipe.Serializer ss = new SmithingTransformRecipe.Serializer();
            ss.toNetwork(pb, original);

            Ingredient.fromNetwork(pb);
            nonnulllist.set(0, Ingredient.fromNetwork(pb));
            nonnulllist.set(1, Ingredient.fromNetwork(pb));

            result = pb.readItem();

            this.recipeId = original.getId();
        }

        @Override
        public boolean matches(Container inv, Level worldIn) {
            return false;
        }

        @Override
        public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
            return result.copy();
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return false;
        }

        @Override
        public ItemStack getResultItem(RegistryAccess p_267052_) {
            return result;
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return nonnulllist;
        }

        @Override
        public ItemStack getToastSymbol() {
            return new ItemStack(Blocks.ANVIL);
        }

        @Override
        public ResourceLocation getId() {
            return this.recipeId;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return null;
        }

        @Override
        public RecipeType<?> getType() {
            return dummy_anvilType;
        }
    }

    static class DummySmithingRecipe extends DummyAnvilRecipe {
        public DummySmithingRecipe(SmithingTransformRecipe recipe) {
            super(recipe);
        }

        @Override
        public ItemStack getToastSymbol() {
            return original.getToastSymbol();
        }

        @Override
        public RecipeType<?> getType() {
            return original.getType();
        }
    }

    static Recipe<?> overrideDummyRecipe(Recipe<?> original) {

        if (!(original instanceof SmithingTransformRecipe))
            return original;

        if (original.getId().getPath().startsWith("anvilcrafting")) {
            return new DummyAnvilRecipe((SmithingTransformRecipe) original);
        } else {
            return new DummySmithingRecipe((SmithingTransformRecipe) original);
        }
    }

    public static class RecipeView {
        final RecipeType<?> recipeType;
        final ResourceLocation background;
        List<Vec3i> slots = Lists.newArrayList();
        final boolean isWideOutputSlot;

        public RecipeView(RecipeType<?> recipeType, ResourceLocation background, List<Vec3i> slots) {
            this(recipeType, background, slots, true);
        }

        public RecipeView(RecipeType<?> recipeType, ResourceLocation background, List<Vec3i> slots,
                          boolean isWideOutputSlot) {
            this.recipeType = recipeType;
            this.background = background;
            this.slots = slots;
            this.isWideOutputSlot = isWideOutputSlot;
        }
    }

    static Map<RecipeType<?>, RecipeView> createRecipeViewMap() {
        Map<RecipeType<?>, RecipeView> map = Maps.newHashMap();

        {
            List<Vec3i> list = Lists.newArrayList();

            // output
            list.add(new Vec3i(124, 35, 0));

            // grid
            int SlotMargin = 18;
            int LeftMargin = 30;
            int TopMargin = 17;

            int RecipeGridX = 3;
            int RecipeGridY = 3;

            for (int i = 0; i < RecipeGridX; ++i) {
                for (int j = 0; j < RecipeGridY; ++j) {
                    list.add(new Vec3i(LeftMargin + j * SlotMargin, TopMargin + i * SlotMargin, 0));
                }
            }

            RecipeType<?> key = RecipeType.CRAFTING;
            map.put(key, new RecipeView(key, GUI_TEXTURE_CRAFTING_TABLE, list));
        }

        {
            List<Vec3i> list = Lists.newArrayList();

            // output
            list.add(new Vec3i(116, 35, 0));
            // input
            list.add(new Vec3i(56, 17, 0));
            // fuel
            list.add(new Vec3i(56, 53, 0));

            {
                RecipeType<?> key = RecipeType.SMELTING;
                map.put(key, new RecipeView(key, GUI_TEXTURE_FURNACE, list));
            }
            {
                RecipeType<?> key = RecipeType.BLASTING;
                map.put(key, new RecipeView(key, GUI_TEXTURE_BLAST_FURNACE, list));
            }
            {
                RecipeType<?> key = RecipeType.SMOKING;
                map.put(key, new RecipeView(key, GUI_TEXTURE_SMOKER, list));
            }
        }

        {
            List<Vec3i> list = Lists.newArrayList();

            // output
            list.add(new Vec3i(134, 47, 0));

            // input
            list.add(new Vec3i(27, 47, 0));
            // material
            list.add(new Vec3i(76, 47, 0));

            {
                RecipeType<?> key = RecipeType.SMITHING;
                map.put(key, new RecipeView(key, GUI_TEXTURE_SMITHING, list, false));
            }

            {
                RecipeType<?> key = dummy_anvilType;
                map.put(key, new RecipeView(key, GUI_TEXTURE_ANVIL, list, false));
            }
        }

        return map;
    }

    @Override
    public void addItemToSlot(Iterator<Ingredient> ingredients, int slotIn, int maxAmount, int y, int x) {
        Ingredient ingredient = ingredients.next();
        if (!ingredient.isEmpty()) {
            if (slotIn < currentView.slots.size()) {

                Vec3i slot = currentView.slots.get(slotIn);
                gr.addIngredient(ingredient, slot.getX(), slot.getY());
            }
        }
    }

    static void clearGhostRecipe() {
        gr.clear();
        currentRecipe = null;
        currentView = null;
    }

    static void setGhostRecipe(ItemStack icon) {
        if (icon == null || !(icon.hasTag() && icon.getTag().contains("Crafting"))) {
            clearGhostRecipe();
            return;
        }
        getInstance().setGhostRecipe(new ResourceLocation(icon.getTag().getString("Crafting")));
    }

    void setGhostRecipe(ResourceLocation loc) {
        if (Objects.equals(loc, currentRecipe)) return;
        currentRecipe = loc;//减少性能消耗
        Optional<? extends Recipe<?>> recipe = MCINSTANCE.level.getRecipeManager().byKey(loc);
        if (!recipe.isPresent()) {
            SlashBlade.LOGGER.warn("[Achievement Recipe Render] Recipe does not exist: {}", loc);
            clearGhostRecipe();
            return;
        }

        gr.clear();
        Recipe<?> iRecipe = recipe.get();
        iRecipe = overrideDummyRecipe(iRecipe);
        gr.setRecipe(iRecipe);
        currentView = typeRecipeViewMap.get(iRecipe.getType());
        if (currentView == null || currentView.slots.size() <= 0) {
            SlashBlade.LOGGER.warn("[Achievement Recipe Render] The GUI display of the current recipe type is not supported: {}", iRecipe.getType());
            clearGhostRecipe();
            return;
        }

        final int outputslotIndex = 0;
        Vec3i outputSlot = currentView.slots.get(outputslotIndex);
        gr.addIngredient(Ingredient.of(iRecipe.getResultItem(null)), outputSlot.getX(), outputSlot.getY());
        this.placeRecipe(3, 3, outputslotIndex, iRecipe, iRecipe.getIngredients().iterator(), 1);
    }

    void drawBackGround(GuiGraphics gg, int xCorner, int yCorner, int zOffset, int xSize, int ySize, int yClip) {
        int bPadding = 5;
        gg.blit(currentView.background, xCorner, yCorner, zOffset, 0, 0, xSize, yClip - bPadding, 256, 256);
        gg.blit(currentView.background, xCorner, yCorner + yClip - bPadding, zOffset, 0, ySize - bPadding, xSize,
                bPadding, 256, 256);
    }

    void drawGhostRecipe(GuiGraphics gg, int xCorner, int yCorner, int zOffset, float partialTicks) {
        try {
            gg.pose().pushPose();
            // matrixStack.translate(0,0,zOffset);

            /*
             * ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
             *
             * float tmp = ir.blitOffset; ir.blitOffset = zOffset - 125;
             */
            int padding = 5;
            gg.renderFakeItem(gr.getRecipe().getToastSymbol(), xCorner + padding, yCorner + padding);

            boolean wideOutputSlot = currentView.isWideOutputSlot;

            gr.render(gg, MCINSTANCE, xCorner, yCorner, wideOutputSlot, partialTicks);

            // ir.blitOffset = tmp;

        } finally {
            gg.pose().popPose();
        }
    }

    void drawTooltip(GuiGraphics gg, int xCorner, int yCorner, int zOffset, int mouseX, int mouseY, Screen gui) {

        ItemStack itemStack = null;

        int slotSize = 16;

        for (int i = 0; i < gr.size(); ++i) {
            GhostRecipe.GhostIngredient ghostIngredient = gr.get(i);
            int j = ghostIngredient.getX() + xCorner;
            int k = ghostIngredient.getY() + yCorner;
            if (mouseX >= j && mouseY >= k && mouseX < j + slotSize && mouseY < k + slotSize) {
                itemStack = ghostIngredient.getItem();
            }
        }

        if (itemStack != null) {
            if (itemStack != null && MCINSTANCE.screen != null) {
                gg.renderTooltip(MCINSTANCE.font, itemStack, mouseX, mouseY);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void onDrawScreenPost(Screen screen, GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof AdvancementsScreen))
            return;
        if (AdvancementsRecipeRenderer.currentRecipe == null)
            return;
        if (AdvancementsRecipeRenderer.currentView == null)
            return;

        AdvancementsScreen gui = (AdvancementsScreen) screen;

        try {

            drawContext.pose().pushPose();

            PoseStack matrixStack = drawContext.pose();

            int zOffset = 425;
            int zStep = 75;
            matrixStack.translate(0, 0, zOffset);

            int xSize = 176;
            int ySize = 166;
            int yClip = 85;

            int xCorner = (gui.width - xSize) / 2;
            int yCorner = (gui.height - yClip) / 2;

            drawBackGround(drawContext, xCorner, yCorner, zOffset, xSize, ySize, yClip);

            drawGhostRecipe(drawContext, xCorner, yCorner, zOffset, tickDelta);

            matrixStack.translate(0, 0, zStep);
            drawTooltip(drawContext, xCorner, yCorner, zOffset, mouseX, mouseY, gui);

        } finally {
            drawContext.pose().popPose();
        }
    }

    @SuppressWarnings("unchecked")
    @Environment(EnvType.CLIENT)
    public void onInitGuiPost(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        if (!(screen instanceof AdvancementsScreen))
            return;

        AdvancementsScreen gui = (AdvancementsScreen) screen;
        ((List<GuiEventListener>) gui.children()).add(new AdvancementsExGuiEventListener(gui));
    }

    public static class AdvancementsExGuiEventListener implements GuiEventListener {
        AdvancementsScreen screen;

        public AdvancementsExGuiEventListener(AdvancementsScreen screen) {
            this.screen = screen;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 1) {
                clearGhostRecipe();
                return false;
            }

            int offsetX = (screen.width - 252) / 2;
            int offsetY = (screen.height - 140) / 2;

            ItemStack found = null;

            AdvancementTab selectedTab = ((AdvancementsScreenAccessor) screen).sb$getSelectedTab();

            if (selectedTab == null)
                return false;

            int mouseXX = (int) (mouseX - offsetX - 9);
            int mouseYY = (int) (mouseY - offsetY - 18);

            double scrollX = ((AdvancementTabAccessor) selectedTab).sb$getScrollX();
            double scrollY = ((AdvancementTabAccessor) selectedTab).sb$getScrollY();
            Map<Advancement, AdvancementWidget> guis = ((AdvancementTabAccessor) selectedTab).sb$getWidgets();

            int i = Mth.floor(scrollX);
            int j = Mth.floor(scrollY);
            if (mouseXX > 0 && mouseXX < 234 && mouseYY > 0 && mouseYY < 113) {
                for (AdvancementWidget advancemententrygui : guis.values()) {
                    if (advancemententrygui.isMouseOver(i, j, mouseXX, mouseYY)) {

                        DisplayInfo info = ((AdvancementWidgetAccessor) advancemententrygui).sb$getDisplay();

                        found = info.getIcon();

                        break;
                    }
                }
            }

            setGhostRecipe(found);

            return false;
        }

        boolean focus = false;

        @Override
        public void setFocused(boolean p_265728_) {
            focus = p_265728_;
        }

        @Override
        public boolean isFocused() {
            return focus;
        }
    }
}
