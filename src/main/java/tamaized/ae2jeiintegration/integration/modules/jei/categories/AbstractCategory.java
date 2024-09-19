package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

public abstract class AbstractCategory<T> implements IRecipeCategory<T> {
	protected final IGuiHelper guiHelper;
	protected final IDrawable icon;
	protected final Component title;
	protected final IDrawable background;

	public AbstractCategory(IGuiHelper guiHelper, ItemLike iconItemLike, Component title, IDrawable background) {
		this.guiHelper = guiHelper;
		this.icon = guiHelper.createDrawableItemLike(iconItemLike);
		this.title = title;
		this.background = background;
	}

	public AbstractCategory(IGuiHelper guiHelper, IDrawable icon, Component title, IDrawable background) {
		this.guiHelper = guiHelper;
		this.icon = icon;
		this.title = title;
		this.background = background;
	}

	@Override
	public final Component getTitle() {
		return title;
	}

	@Override
	public final IDrawable getBackground() {
		return background;
	}

	@Override
	public final IDrawable getIcon() {
		return icon;
	}
}