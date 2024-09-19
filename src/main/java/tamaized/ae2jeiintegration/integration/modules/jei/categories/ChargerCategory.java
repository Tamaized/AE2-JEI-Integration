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
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.LabelDrawable;

public class ChargerCategory extends AbstractCategory<RecipeHolder<ChargerRecipe>> {
    public static final RecipeType<RecipeHolder<ChargerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.CHARGER);

    private final IDrawableStatic unfilledArrow;
    private final IDrawable turnsLabel;

    public ChargerCategory(IGuiHelper guiHelper) {
        super(
            guiHelper,
            AEBlocks.CHARGER,
            AEBlocks.CHARGER.stack().getHoverName(),
            guiHelper.createBlankDrawable(130, 50)
        );
        this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);

        var turnCount = (ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + CrankBlockEntity.POWER_PER_CRANK_TURN - 1)
            / CrankBlockEntity.POWER_PER_CRANK_TURN;
        MutableComponent turnsText = Component.literal(turnCount + " turns or " + ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + " AE");
        this.turnsLabel = new LabelDrawable(turnsText)
            .bodyColor()
            .noShadow()
            .alignLeft();
    }

    @Override
    public RecipeType<RecipeHolder<ChargerRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChargerRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();

        builder.addSlot(RecipeIngredientRole.INPUT, 31, 8)
            .setStandardSlotBackground()
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 8)
            .setStandardSlotBackground()
            .addItemStack(recipe.getResultItem());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 3, 30)
            .addItemStack(AEBlocks.CRANK.stack());
    }

    @Override
    public void draw(RecipeHolder<ChargerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        unfilledArrow.draw(guiGraphics, 52, 8);
        turnsLabel.draw(guiGraphics, 20, 35);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<ChargerRecipe> holder) {
        return holder.id();
    }
}
