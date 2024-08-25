package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;

public final class WidgetFactory {
    private final IGuiHelper guiHelper;
    private final IDrawableStatic unfilledArrow;

    public WidgetFactory(IJeiHelpers jeiHelpers) {
        this.guiHelper = jeiHelpers.getGuiHelper();
        this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
    }

    public DrawableWidget drawable(int x, int y, IDrawable drawable) {
        return new DrawableWidget(drawable, x, y);
    }

    public Label label(int x, int y, Component text) {
        return new Label(x, y, text);
    }

    public DrawableWidget item(int x, int y, ItemStack stack) {
        return new DrawableWidget(guiHelper.createDrawableItemStack(stack), x, y);
    }

    /**
     * An unfilled recipe arrow.
     */
    public DrawableWidget unfilledArrow(int x, int y) {
        return new DrawableWidget(unfilledArrow, x, y);
    }
}
