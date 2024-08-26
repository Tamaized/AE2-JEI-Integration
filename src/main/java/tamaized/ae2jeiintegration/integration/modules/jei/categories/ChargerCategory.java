package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.ChargerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Label;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

public class ChargerCategory implements IRecipeCategory<RecipeHolder<ChargerRecipe>> {
    public static final RecipeType<RecipeHolder<ChargerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.CHARGER);

    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawableStatic unfilledArrow;
    private final WidgetFactory widgetFactory;

    public ChargerCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(130, 50);
        this.icon = guiHelper.createDrawableItemStack(AEBlocks.CHARGER.stack());
        this.slotBackground = guiHelper.getSlotDrawable();
        this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
        this.widgetFactory = new WidgetFactory(helpers);
    }

    @Override
    public RecipeType<RecipeHolder<ChargerRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return AEBlocks.CHARGER.stack().getHoverName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChargerRecipe> holder, IFocusGroup focuses) {
        ChargerRecipe recipe = holder.value();

        builder.addSlot(RecipeIngredientRole.INPUT, 31, 8)
            .setBackground(slotBackground, -1, -1)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 8)
            .setBackground(slotBackground, -1, -1)
            .addItemStack(recipe.getResultItem());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 3, 30)
            .addItemStack(AEBlocks.CRANK.stack());
    }

    @Override
    public void draw(RecipeHolder<ChargerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        unfilledArrow.draw(guiGraphics, 52, 8);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<ChargerRecipe> recipe, IFocusGroup focuses) {
        var turns = (ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + CrankBlockEntity.POWER_PER_CRANK_TURN - 1)
            / CrankBlockEntity.POWER_PER_CRANK_TURN;
        Label label = widgetFactory
            .label(20, 35,
                Component.literal(
                    turns + " turns or " + ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + " AE"))
            .bodyColor()
            .noShadow()
            .alignLeft();

        builder.addWidget(label);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<ChargerRecipe> holder) {
        return holder.id();
    }
}
