package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;

public interface View {
    default void createRecipeExtras(IRecipeExtrasBuilder builder, WidgetFactory factory, IFocusGroup focuses) {

    }

    default void buildSlots(IRecipeLayoutBuilder builder) {
    }
}
