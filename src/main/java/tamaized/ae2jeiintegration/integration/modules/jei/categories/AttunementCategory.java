package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ItemModText;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.recipes.AttunementDisplay;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;

public class AttunementCategory implements IRecipeCategory<AttunementDisplay> {

    public static final RecipeType<AttunementDisplay> TYPE = RecipeType.create(AppEng.MOD_ID, "attunement",
            AttunementDisplay.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawableStatic unfilledArrow;

    public AttunementCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(130, 36);
        this.icon = guiHelper.createDrawableItemStack(AEParts.ME_P2P_TUNNEL.stack());
        this.slotBackground = guiHelper.getSlotDrawable();
        this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Component getTitle() {
        return ItemModText.P2P_TUNNEL_ATTUNEMENT.text();
    }

    @Override
    public RecipeType<AttunementDisplay> getRecipeType() {
        return TYPE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AttunementDisplay recipe, IFocusGroup focuses) {
        var x = background.getWidth() / 2 - 41;
        var y = background.getHeight() / 2 - 13;

        builder.addSlot(RecipeIngredientRole.INPUT, x + 4, y + 5)
            .setBackground(slotBackground, -1, -1)
            .addIngredients(recipe.inputs())
            .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                for (Component line : recipe.description()) {
                    tooltip.add(line);
                }
            });
        builder.addSlot(RecipeIngredientRole.OUTPUT, x + 61, y + 5)
            .setBackground(slotBackground, -1, -1)
            .addItemStack(new ItemStack(recipe.tunnel()));
    }

    @Override
    public void draw(AttunementDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var x = background.getWidth() / 2 - 41;
        var y = background.getHeight() / 2 - 13;
        unfilledArrow.draw(guiGraphics, x + 27, y + 4);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(AttunementDisplay recipe) {
        return recipe.uid();
    }
}
