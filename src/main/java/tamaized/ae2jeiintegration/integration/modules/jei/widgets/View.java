package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import java.util.List;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.client.gui.GuiGraphics;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public interface View {
    default void createWidgets(WidgetFactory factory, List<Widget> widgets) {
    }

    default void buildSlots(IRecipeLayoutBuilder builder) {
    }

    default void draw(GuiGraphics guiGraphics, IRecipeSlotsView slots, double mouseX, double mouseY) {
    }

    default boolean getTooltipStrings(ITooltipBuilder tooltipBuilder, double mouseX, double mouseY) {
        return false;
    }
}
