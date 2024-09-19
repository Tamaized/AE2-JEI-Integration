package tamaized.ae2jeiintegration.integration.modules.jei.drawables;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class LabelDrawable implements IDrawable {
	public final Component text;
	private final Font font;
	public int color = -1;
	public int maxWidth = -1;
	public boolean shadow = true;
	private LabelDrawable.LabelAlignment align = LabelDrawable.LabelAlignment.CENTER;
	private List<FormattedLine> formattedLines = null;

	public LabelDrawable(Component text) {
		this.text = text;
		this.font = Minecraft.getInstance().font;
	}

	public Rect2i getBounds() {
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		for (var formattedLine : getLines()) {
			top = Math.min(top, (int) formattedLine.y);
			left = Math.min(left, (int) formattedLine.x);
			bottom = Math.max(bottom, (int) (formattedLine.y + formattedLine.height));
			right = Math.max(right, (int) (formattedLine.x + formattedLine.width));
		}
		int width = right - left;
		int height = bottom - top;
		return new Rect2i(left, top, width, height);
	}

	public int getX() {
		int left = Integer.MAX_VALUE;
		for (var formattedLine : getLines()) {
			left = Math.min(left, (int) formattedLine.x);
		}
		return left;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
		for (var line : getLines()) {
			var font = Minecraft.getInstance().font;
			guiGraphics.drawString(font, line.text, (int) line.x + xOffset, (int) line.y + + yOffset, color, shadow);
		}
	}

	@Override
	public int getWidth() {
		return getBounds().getWidth();
	}

	@Override
	public int getHeight() {
		return getBounds().getHeight();
	}

	public LabelDrawable bodyColor() {
		color = 0x7E7E7E;
		return this;
	}

	public LabelDrawable alignLeft() {
		align = LabelDrawable.LabelAlignment.LEFT;
		return this;
	}

	public LabelDrawable alignRight() {
		align = LabelDrawable.LabelAlignment.RIGHT;
		return this;
	}

	public LabelDrawable noShadow() {
		shadow = false;
		return this;
	}

	public LabelDrawable bodyText() {
		noShadow();
		bodyColor();
		return this;
	}

	private float getAlignedX(int width) {
		return switch (align) {
			case LEFT -> 0;
			case CENTER -> -width / 2f;
			case RIGHT -> -width;
		};
	}

	public LabelDrawable maxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	private enum LabelAlignment {
		LEFT,
		CENTER,
		RIGHT
	}

	/**
	 * Lazily apply the max-width using current settings and split into a line-based layout.
	 */
	private List<FormattedLine> getLines() {
		if (formattedLines != null) {
			return formattedLines;
		}

		if (maxWidth == -1) {
			var formattedText = text.getVisualOrderText();
			var width = font.width(formattedText);
			formattedLines = List.of(
				new FormattedLine(formattedText, getAlignedX(width), 0, width, font.lineHeight));
		} else {
			var splitLines = font.split(text, maxWidth);
			var formattedLines = new ArrayList<FormattedLine>(splitLines.size());
			for (int i = 0; i < splitLines.size(); i++) {
				var splitLine = splitLines.get(i);
				var width = font.width(splitLine);
				formattedLines.add(new FormattedLine(
					splitLine,
					getAlignedX(width),
					i * font.lineHeight,
					width,
					font.lineHeight));
			}
			this.formattedLines = formattedLines;
		}

		return formattedLines;
	}

	private record FormattedLine(FormattedCharSequence text, float x, float y, int width, int height) {
	}
}
