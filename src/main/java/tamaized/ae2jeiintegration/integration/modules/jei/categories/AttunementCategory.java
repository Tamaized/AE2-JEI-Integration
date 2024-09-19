package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ItemModText;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.recipes.AttunementRecipe;

public class AttunementCategory extends AbstractCategory<AttunementRecipe> {

    public static final RecipeType<AttunementRecipe> RECIPE_TYPE = RecipeType.create(AppEng.MOD_ID, "attunement",
            AttunementRecipe.class);

    private final IDrawableStatic unfilledArrow;

    public AttunementCategory(IGuiHelper guiHelper) {
        super(
            guiHelper,
            AEParts.ME_P2P_TUNNEL,
            ItemModText.P2P_TUNNEL_ATTUNEMENT.text(),
            guiHelper.createBlankDrawable(130, 36)
        );
        this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
    }

    @Override
    public RecipeType<AttunementRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AttunementRecipe recipe, IFocusGroup focuses) {
        var x = background.getWidth() / 2 - 41;
        var y = background.getHeight() / 2 - 13;

        builder.addSlot(RecipeIngredientRole.INPUT, x + 4, y + 5)
            .setStandardSlotBackground()
            .addIngredients(recipe.inputs())
            .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                for (Component line : recipe.description()) {
                    tooltip.add(line);
                }
            });
        builder.addSlot(RecipeIngredientRole.OUTPUT, x + 61, y + 5)
            .setStandardSlotBackground()
            .addItemStack(new ItemStack(recipe.tunnel()));
    }

    @Override
    public void draw(AttunementRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var x = background.getWidth() / 2 - 41;
        var y = background.getHeight() / 2 - 13;
        unfilledArrow.draw(guiGraphics, x + 27, y + 4);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(AttunementRecipe recipe) {
        return recipe.uid();
    }
}
