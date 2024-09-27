package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.transform.TransformRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import tamaized.ae2jeiintegration.integration.modules.jei.Colors;
import tamaized.ae2jeiintegration.integration.modules.jei.FluidBlockRenderer;

public class TransformCategory extends AbstractRecipeCategory<RecipeHolder<TransformRecipe>> {
    public static final RecipeType<RecipeHolder<TransformRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.TRANSFORM);

    private final FluidBlockRenderer fluidRenderer;

    public TransformCategory(IGuiHelper guiHelper) {
        super(
            RECIPE_TYPE,
            ItemModText.TRANSFORM_CATEGORY.text(),
            guiHelper.createDrawableItemLike(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED),
            130,
            62
        );
        fluidRenderer = new FluidBlockRenderer();
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
            builder.addInputSlot(x + 1, y + 1)
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
                .addItemLike(Blocks.TNT)
                .addItemLike(AEBlocks.TINY_TNT);
        }

        builder.addOutputSlot(105 + 1, yOffset + 1)
            .setStandardSlotBackground()
            .addItemStack(recipe.getResultItem());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<TransformRecipe> holder, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
        var recipe = holder.value();

        // Second column is arrow pointing into water
        builder.addRecipeArrow(25, 23);
        // Fourth column is arrow pointing to results
        builder.addRecipeArrow(76, 23);

        Component circumstance;
        if (recipe.circumstance.isExplosion()) {
            circumstance = ItemModText.EXPLOSION.text();
        } else {
            circumstance = ItemModText.SUBMERGE_IN.text();
        }

        // Text label describing the transform circumstances
        builder.addText(circumstance, 24, 0, getWidth() - 48, 20)
            .alignHorizontalCenter()
            .alignVerticalCenter()
            .setColor(Colors.BODY);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<TransformRecipe> holder) {
        return holder.id();
    }
}
