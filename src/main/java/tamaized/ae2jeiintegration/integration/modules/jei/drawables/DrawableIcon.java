package tamaized.ae2jeiintegration.integration.modules.jei.drawables;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class DrawableIcon implements IDrawable {
	private final int width;
	private final int height;
	private final Blitter blitter;

	public DrawableIcon(Icon icon) {
		this.width = icon.width;
		this.height = icon.height;
		this.blitter = icon.getBlitter();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void draw(GuiGraphics guiGraphics) {
		blitter.blit(guiGraphics);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
		blitter.dest(xOffset, yOffset).blit(guiGraphics);
	}
}
