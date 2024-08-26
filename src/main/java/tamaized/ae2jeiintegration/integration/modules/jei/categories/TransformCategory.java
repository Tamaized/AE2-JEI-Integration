package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.transform.TransformRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import tamaized.ae2jeiintegration.integration.modules.jei.FluidBlockRenderer;
import tamaized.ae2jeiintegration.integration.modules.jei.JEIPlugin;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Label;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

public class TransformCategory implements IRecipeCategory<RecipeHolder<TransformRecipe>> {

    public static final RecipeType<RecipeHolder<TransformRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.TRANSFORM);

    private final IDrawable icon;

    private final IDrawable background;

	private final IDrawable slotBackground;

    private final IPlatformFluidHelper<?> fluidHelper;

    private final FluidBlockRenderer fluidRenderer;
    private final IDrawableStatic unfilledArrow;
    private final WidgetFactory widgetFactory;

    public TransformCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        background = guiHelper.createBlankDrawable(130, 62);
        slotBackground = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 34, 18, 18);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.stack());
        fluidHelper = helpers.getPlatformFluidHelper();
        fluidRenderer = new FluidBlockRenderer();
        unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
        widgetFactory = new WidgetFactory(helpers);
    }

    @Override
    public RecipeType<RecipeHolder<TransformRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TransformRecipe> holder, IFocusGroup focuses) {
        TransformRecipe recipe = holder.value();

        int yOffset = 23;
        var slotIndex = 0;

        var y = 5;
        var x = 5;
        if (recipe.getIngredients().size() < 3) {
            // so ingredients lists with less than two rows get centered vertically
            y += (3 - recipe.getIngredients().size()) * 18 / 2;
        }
        for (var input : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x + 1, y + 1)
                .setSlotName("input" + (slotIndex++))
                .setBackground(slotBackground, -1, -1)
                .addIngredients(input);
            y += 18;
            if (y >= 54) {
                // we don't actually have room to make multiple columns of ingredients look nice,
                // but this is better than just overflowing downwards.
                y -= 54;
                x += 18;
            }
        }

        if (recipe.circumstance.isFluid()) {
            var slot = builder.addSlot(RecipeIngredientRole.CATALYST, 55 + 1, yOffset + 1)
                .setSlotName("fluid");

            for (var fluid : recipe.circumstance.getFluidsForRendering()) {
                if (!fluid.isSource(fluid.defaultFluidState()))
                    continue;
                slot.addFluidStack(fluid, fluidHelper.bucketVolume());
            }

            slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, fluidRenderer);
        } else if (recipe.circumstance.isExplosion()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 55 + 1, yOffset + 1)
                .setSlotName("explosion")
                .addItemStack(new ItemStack(Blocks.TNT))
                .addItemStack(AEBlocks.TINY_TNT.stack());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 105 + 1, yOffset + 1)
            .setSlotName("output")
            .setBackground(slotBackground, -1, -1)
            .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(RecipeHolder<TransformRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int yOffset = 23;
        // Second column is arrow pointing into water
        unfilledArrow.draw(guiGraphics, 25, yOffset);
        // Fourth column is arrow pointing to results
        unfilledArrow.draw(guiGraphics, 76, yOffset);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<TransformRecipe> holder, IFocusGroup focuses) {
        TransformRecipe recipe = holder.value();

        Component circumstance;
        if (recipe.circumstance.isExplosion()) {
            circumstance = ItemModText.EXPLOSION.text();
        } else {
            circumstance = ItemModText.SUBMERGE_IN.text();
        }

        // Text label describing the transform circumstances
        Label widget = widgetFactory.label(background.getWidth() / 2, 5, circumstance)
            .bodyText();
        builder.addWidget(widget);
    }

    @Override
    public Component getTitle() {
        return ItemModText.TRANSFORM_CATEGORY.text();
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<TransformRecipe> holder) {
        return holder.id();
    }
}
