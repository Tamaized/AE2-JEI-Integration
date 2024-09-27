package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ItemModText;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.recipes.AttunementRecipe;

public class AttunementCategory extends AbstractRecipeCategory<AttunementRecipe> {

    public static final RecipeType<AttunementRecipe> RECIPE_TYPE = RecipeType.create(AppEng.MOD_ID, "attunement",
            AttunementRecipe.class);

    public AttunementCategory(IGuiHelper guiHelper) {
        super(
            RECIPE_TYPE,
            ItemModText.P2P_TUNNEL_ATTUNEMENT.text(),
            guiHelper.createDrawableItemLike(AEParts.ME_P2P_TUNNEL),
            130,
            36
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AttunementRecipe recipe, IFocusGroup focuses) {
        var x = getWidth() / 2 - 41;
        var y = getHeight() / 2 - 13;

        builder.addInputSlot(x + 4, y + 5)
            .setStandardSlotBackground()
            .addIngredients(recipe.inputs())
            .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                for (Component line : recipe.description()) {
                    tooltip.add(line);
                }
            });

        builder.addOutputSlot(x + 61, y + 5)
            .setStandardSlotBackground()
            .addItemLike(recipe.tunnel());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AttunementRecipe recipe, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
		builder.addRecipeArrow(getWidth() / 2 - 12, getHeight() / 2 - 9);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(AttunementRecipe recipe) {
        return recipe.uid();
    }
}
