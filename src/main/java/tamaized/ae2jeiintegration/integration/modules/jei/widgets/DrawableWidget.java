package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;

public class DrawableWidget extends AE2RecipeWidget {
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
