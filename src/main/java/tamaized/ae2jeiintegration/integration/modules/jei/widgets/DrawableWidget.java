package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;

public class DrawableWidget implements IRecipeWidget {
    private final IDrawable drawable;
    private final ScreenPosition position;

    public DrawableWidget(IDrawable drawable, int x, int y) {
        this.drawable = drawable;
        this.position = new ScreenPosition(x, y);
    }

    @Override
    public ScreenPosition getPosition() {
        return position;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        drawable.draw(guiGraphics);
    }
}
