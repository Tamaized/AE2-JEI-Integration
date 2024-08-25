package tamaized.ae2jeiintegration.integration.modules.jei.transfer;

import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractTransferHandler {
    protected static final int CRAFTING_GRID_WIDTH = 3;
    protected static final int CRAFTING_GRID_HEIGHT = 3;

    protected final boolean fitsIn3x3Grid(Recipe<?> recipe) {
		return recipe.canCraftInDimensions(CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT);
	}
}
