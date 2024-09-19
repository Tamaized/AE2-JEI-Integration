package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.StackWithBounds;
import appeng.client.gui.implementations.InscriberScreen;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.InscriberRecipeCategory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

class AEGuiContainerHandler implements IGuiContainerHandler<AEBaseScreen<?>> {
	private final IIngredientManager ingredientManager;

	public AEGuiContainerHandler(IIngredientManager ingredientManager) {
		this.ingredientManager = ingredientManager;
	}

	@Override
	public List<Rect2i> getGuiExtraAreas(AEBaseScreen<?> screen) {
		return screen.getExclusionZones();
	}

	@Override
	public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(AEBaseScreen<?> screen,
																			  double mouseX, double mouseY) {
		// The following code allows the player to show recipes involving fluids in AE fluid terminals
		// or AE fluid tanks shown in fluid interfaces and other UI.
		var stackWithBounds = screen.getStackUnderMouse(mouseX, mouseY);
		if (stackWithBounds != null) {
			return makeClickableIngredient(stackWithBounds);
		}

		return Optional.empty();
	}

	@Override
	public Collection<IGuiClickableArea> getGuiClickableAreas(AEBaseScreen<?> screen, double mouseX, double mouseY) {
		if (screen instanceof InscriberScreen) {
			return List.of(
				IGuiClickableArea.createBasic(82, 39, 26, 16, InscriberRecipeCategory.RECIPE_TYPE));
		}

		return List.of();
	}

	private Optional<IClickableIngredient<?>> makeClickableIngredient(StackWithBounds stackWithBounds) {
		var ingredient = GenericEntryStackHelper.stackToClickableIngredient(ingredientManager, stackWithBounds);
		return Optional.ofNullable(ingredient);
	}
}
