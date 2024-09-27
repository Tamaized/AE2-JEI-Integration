package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.handlers.ChargerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import tamaized.ae2jeiintegration.integration.modules.jei.Colors;

public class ChargerCategory extends AbstractRecipeCategory<RecipeHolder<ChargerRecipe>> {
    public static final RecipeType<RecipeHolder<ChargerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.CHARGER);

    public ChargerCategory(IGuiHelper guiHelper) {
        super(
            RECIPE_TYPE,
            AEBlocks.CHARGER.stack().getHoverName(),
            guiHelper.createDrawableItemLike(AEBlocks.CHARGER),
            130,
            50
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ChargerRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();

        builder.addInputSlot(31, 8)
            .setStandardSlotBackground()
            .addIngredients(recipe.getIngredient());

        builder.addOutputSlot(81, 8)
            .setStandardSlotBackground()
            .addItemStack(recipe.getResultItem());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 3, 30)
            .addItemLike(AEBlocks.CRANK);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<ChargerRecipe> recipe, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
        builder.addRecipeArrow(52, 8);

        var turnCount = (ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + CrankBlockEntity.POWER_PER_CRANK_TURN - 1)
            / CrankBlockEntity.POWER_PER_CRANK_TURN;
        MutableComponent turnsText = Component.literal(turnCount + " turns or " + ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + " AE");
        builder.addText(turnsText, 20, 35, getWidth() - 20, 10)
            .setColor(Colors.BODY);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<ChargerRecipe> holder) {
        return holder.id();
    }
}
