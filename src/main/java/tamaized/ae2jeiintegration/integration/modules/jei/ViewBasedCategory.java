package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.util.Platform;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.View;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Widget;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewBasedCategory<T> implements IRecipeCategory<T> {
    private final WidgetFactory widgetFactory;

    private final LoadingCache<T, CachedView> cache;

    protected final IGuiHelper guiHelper;

    protected ViewBasedCategory(IJeiHelpers helpers) {
        this.guiHelper = helpers.getGuiHelper();
        widgetFactory = new WidgetFactory(helpers);
        cache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .build(new CacheLoader<>() {
                    @Override
                    public CachedView load(T recipe) {
                        return new CachedView(getView(recipe));
                    }
                });
    }

    protected abstract View getView(T recipe);

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        cache.getUnchecked(recipe).view.buildSlots(builder);
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        var cachedView = cache.getUnchecked(recipe);
        for (var widget : getWidgets(cachedView)) {
            widget.draw(guiGraphics);
        }
        cachedView.view.draw(guiGraphics, recipeSlotsView, mouseX, mouseY);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var cachedView = cache.getUnchecked(recipe);

        if (cachedView.view.getTooltipStrings(tooltip, mouseX, mouseY)) {
            return;
        }

        var widgets = getWidgets(cachedView);
        for (int i = widgets.size() - 1; i >= 0; i--) {
            var widget = widgets.get(i);
            if (widget.hitTest(mouseX, mouseY)) {
                if (widget.getTooltipLines(tooltip)) {
                    return;
                }
            }
        }
    }

    private List<Widget> getWidgets(CachedView cachedView) {
        // Always re-create the widgets in a dev-env for faster prototyping.
        // otherwise cache them.
        if (Platform.isDevelopmentEnvironment() || cachedView.widgets == null) {
            cachedView.widgets = new ArrayList<>();
            cachedView.view.createWidgets(widgetFactory, cachedView.widgets);
        }
        return cachedView.widgets;
    }

    private static class CachedView {
        private final View view;
        public List<Widget> widgets;

        public CachedView(View view) {
            this.view = view;
        }
    }
}
