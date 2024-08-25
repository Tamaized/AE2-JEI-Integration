package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.View;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

public abstract class ViewBasedCategory<T> implements IRecipeCategory<T> {
    private final WidgetFactory widgetFactory;

    protected ViewBasedCategory(IJeiHelpers helpers) {
        this.widgetFactory = new WidgetFactory(helpers);
    }

    protected abstract View getView(T recipe);

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        View view = getView(recipe);
        view.buildSlots(builder);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        var view = getView(recipe);
        view.createRecipeExtras(builder, widgetFactory, focuses);
    }
}
