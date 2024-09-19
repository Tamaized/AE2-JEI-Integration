package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.helpers.IGuiHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;

public final class WidgetFactory {
    private WidgetFactory() {}

    /**
     * An unfilled recipe arrow.
     */
    public static DrawableWidget unfilledArrow(IGuiHelper guiHelper, int x, int y) {
        var unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
        return new DrawableWidget(unfilledArrow, x, y);
    }
}
