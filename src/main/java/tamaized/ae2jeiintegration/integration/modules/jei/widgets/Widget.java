package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.client.gui.GuiGraphics;

public interface Widget {
    void draw(GuiGraphics guiGraphics);

    default boolean hitTest(double x, double y) {
        return false;
    }

    default boolean getTooltipLines(ITooltipBuilder tooltipBuilder) {
        return false;
    }
}
