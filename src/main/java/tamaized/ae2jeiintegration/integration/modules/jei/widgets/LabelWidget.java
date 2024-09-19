package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.LabelDrawable;

public class LabelWidget implements IRecipeWidget {
    public final ScreenPosition position;
    public final LabelDrawable labelDrawable;

    public LabelWidget(int x, int y, Component text) {
        this.position = new ScreenPosition(x, y);
        this.labelDrawable = new LabelDrawable(text);
    }

    @Override
    public ScreenPosition getPosition() {
        return position;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        labelDrawable.draw(guiGraphics);
    }

    public LabelWidget bodyColor() {
        labelDrawable.bodyColor();
        return this;
    }

    public LabelWidget alignLeft() {
        labelDrawable.alignLeft();
        return this;
    }

    public LabelWidget alignRight() {
        labelDrawable.alignRight();
        return this;
    }

    public LabelWidget noShadow() {
        labelDrawable.noShadow();
        return this;
    }

    public LabelWidget bodyText() {
        labelDrawable.bodyText();
        return this;
    }

    public LabelWidget maxWidth(int maxWidth) {
        labelDrawable.maxWidth(maxWidth);
        return this;
    }
}
