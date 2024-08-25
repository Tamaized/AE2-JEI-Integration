package tamaized.ae2jeiintegration.integration.modules.jei.drawables;

import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.JEIPlugin;

public class DrawableHelper {
	private static @Nullable IDrawableStatic filledArrow;
	private static @Nullable IDrawableStatic unfilledArrow;

	public static IDrawableStatic getUnfilledArrow(IGuiHelper guiHelper) {
		if (unfilledArrow == null) {
			unfilledArrow = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 17, 24, 17);
		}
		return unfilledArrow;
	}

	public static IDrawableStatic getFilledArrow(IGuiHelper guiHelper) {
		if (filledArrow == null) {
			filledArrow = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 0, 24, 17);
		}
		return filledArrow;
	}
}
