package tamaized.ae2jeiintegration.integration.modules.jei.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class Label extends AE2RecipeWidget {
    public final ScreenPosition position;
    public final Component text;
    private final Font font;
    public int color = -1;
    public int maxWidth = -1;
    public boolean shadow = true;
    private LabelAlignment align = LabelAlignment.CENTER;
    private List<FormattedLine> formattedLines = null;

    public Label(int x, int y, Component text) {
        this.position = new ScreenPosition(x, y);
        this.text = text;
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public ScreenPosition getPosition() {
        return position;
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
        left += position.x();
        top += position.y();
        return new Rect2i(left, top, width, height);
    }


    @Override
    public void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        for (var line : getLines()) {
            var font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, line.text, (int) line.x, (int) line.y, color, shadow);
        }
    }

    public Label bodyColor() {
        color = 0x7E7E7E;
        return this;
    }

    public Label alignLeft() {
        align = LabelAlignment.LEFT;
        return this;
    }

    public Label alignRight() {
        align = LabelAlignment.RIGHT;
        return this;
    }

    public Label noShadow() {
        shadow = false;
        return this;
    }

    public Label bodyText() {
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

    public Label maxWidth(int maxWidth) {
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
