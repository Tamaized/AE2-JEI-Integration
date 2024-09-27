package tamaized.ae2jeiintegration.integration.modules.jei.drawables;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

/**
 * A renderer that cycles through a list of item stacks.
 */
public class CyclingDrawable implements IDrawable {
    private final List<IDrawable> stages;
    private final ITickTimer tickTimer;

    public CyclingDrawable(List<IDrawable> stages, ITickTimer tickTimer) {
        this.stages = stages;
        this.tickTimer = tickTimer;
    }

    public static CyclingDrawable forItems(IGuiHelper guiHelper, ItemLike... items) {
        List<IDrawable> stages = Arrays.stream(items)
            .map(guiHelper::createDrawableItemLike)
            .toList();
        ITickTimer tickTimer = guiHelper.createTickTimer(100, stages.size() - 1, false);
        return new CyclingDrawable(stages, tickTimer);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        IDrawable stage = stages.get(tickTimer.getValue());
        stage.draw(guiGraphics, xOffset, yOffset);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}
