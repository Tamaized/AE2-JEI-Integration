package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.blockentity.misc.ChargerBlockEntity;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.handlers.ChargerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.View;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Widget;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

import java.util.List;

public class ChargerCategory extends ViewBasedCategory<RecipeHolder<ChargerRecipe>> {

    public static final RecipeType<RecipeHolder<ChargerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(ChargerRecipe.TYPE);
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final IDrawable slotBackground;

    public ChargerCategory(IJeiHelpers helpers) {
        super(helpers);
        var guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createBlankDrawable(130, 50);
        this.icon = guiHelper.createDrawableItemStack(AEBlocks.CHARGER.stack());
        this.slotBackground = guiHelper.getSlotDrawable();
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
    protected View getView(RecipeHolder<ChargerRecipe> holder) {
        ChargerRecipe recipe = holder.value();
        return new View() {
            @Override
            public void buildSlots(IRecipeLayoutBuilder builder) {
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
            public void createWidgets(WidgetFactory factory, List<Widget> widgets) {
                widgets.add(factory.unfilledArrow(52, 8));

                var turns = (ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + CrankBlockEntity.POWER_PER_CRANK_TURN - 1)
                        / CrankBlockEntity.POWER_PER_CRANK_TURN;
                widgets.add(factory
                        .label(20, 35,
                                Component.literal(
                                        turns + " turns or " + ChargerBlockEntity.POWER_MAXIMUM_AMOUNT + " AE"))
                        .bodyColor()
                        .noShadow()
                        .alignLeft());
            }
        };
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<ChargerRecipe> holder) {
        return holder.id();
    }
}
