package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import java.util.List;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.network.chat.Component;

public abstract class AbstractWidget implements Widget {
    public List<Component> tooltipLines = List.of();

    protected final void setTooltipLines(List<Component> tooltipLines) {
        this.tooltipLines = tooltipLines;
    }

    @Override
    public boolean getTooltipLines(ITooltipBuilder tooltipBuilder) {
        if (tooltipLines.isEmpty()) {
            return false;
        }
        tooltipBuilder.addAll(tooltipLines);
        return true;
    }
}
