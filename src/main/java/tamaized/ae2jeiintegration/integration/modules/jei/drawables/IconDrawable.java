package tamaized.ae2jeiintegration.integration.modules.jei.drawables;

import net.minecraft.client.gui.GuiGraphics;

import mezz.jei.api.gui.drawable.IDrawable;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;

/**
 * Creates {@link IDrawable} from {@link Icon}
 */
public final class IconDrawable implements IDrawable {
    private final Blitter blitter;
    private final int x;
    private final int y;

    public IconDrawable(Icon icon, int x, int y) {
        this.blitter = icon.getBlitter();
        this.x = x;
        this.y = y;
    }

    @Override
    public int getWidth() {
        return blitter.getSrcWidth();
    }

    @Override
    public int getHeight() {
        return blitter.getSrcHeight();
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        blitter.dest(x + xOffset, y + yOffset).blit(guiGraphics);
    }
}
