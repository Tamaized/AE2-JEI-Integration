package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.transform.TransformRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import tamaized.ae2jeiintegration.integration.modules.jei.FluidBlockRenderer;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.LabelWidget;

public class TransformCategory extends AbstractCategory<RecipeHolder<TransformRecipe>> {
    public static final RecipeType<RecipeHolder<TransformRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.TRANSFORM);

    private final FluidBlockRenderer fluidRenderer;
    private final IDrawableStatic unfilledArrow;

    public TransformCategory(IGuiHelper guiHelper) {
        super(
            guiHelper,
            AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED,
            ItemModText.TRANSFORM_CATEGORY.text(),
            guiHelper.createBlankDrawable(130, 62)
        );
        fluidRenderer = new FluidBlockRenderer();
        unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
    }

    @Override
    public RecipeType<RecipeHolder<TransformRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TransformRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();

        int yOffset = 23;

        var y = 5;
        var x = 5;
        if (recipe.getIngredients().size() < 3) {
            // so ingredients lists with less than two rows get centered vertically
            y += (3 - recipe.getIngredients().size()) * 18 / 2;
        }
        for (var input : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x + 1, y + 1)
                .setStandardSlotBackground()
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
            var slot = builder.addSlot(RecipeIngredientRole.CATALYST, 55 + 1, yOffset + 1);

            for (var fluid : recipe.circumstance.getFluidsForRendering()) {
                if (!fluid.isSource(fluid.defaultFluidState()))
                    continue;
                slot.addFluidStack(fluid);
            }

            slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, fluidRenderer);
        } else if (recipe.circumstance.isExplosion()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 55 + 1, yOffset + 1)
                .addItemStack(new ItemStack(Blocks.TNT))
                .addItemStack(AEBlocks.TINY_TNT.stack());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 105 + 1, yOffset + 1)
            .setStandardSlotBackground()
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
        var recipe = holder.value();

        Component circumstance;
        if (recipe.circumstance.isExplosion()) {
            circumstance = ItemModText.EXPLOSION.text();
        } else {
            circumstance = ItemModText.SUBMERGE_IN.text();
        }

        // Text label describing the transform circumstances
        int x = background.getWidth() / 2;
        var widget = new LabelWidget(x, 5, circumstance)
            .bodyText();
        builder.addWidget(widget);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<TransformRecipe> holder) {
        return holder.id();
    }
}
