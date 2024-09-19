package tamaized.ae2jeiintegration.integration.modules.jei.transfer;

import appeng.core.localization.ItemModText;
import appeng.integration.modules.itemlists.CraftingHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.items.CraftingTermMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Objects;
import java.util.Optional;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;

/**
 * Recipe transfer implementation with the intended purpose of actually crafting an item. Most of the work is done
 * server-side because permission-checks and inventory extraction cannot be done client-side.
 * <p>
 * Here is how it works, depending on the various cases. In any case, we highlight missing entries in red and craftable
 * entries in blue. How the {@code +} button is rendered and what it does depends on various cases:
 * <ul>
 * <li><b>All items are present:</b> normal gray, can click + to move.</li>
 * <li><b>All items are missing:</b> red, can't click.</li>
 * <li><b>Some items are missing, all craftable:</b> blue, can click to move what's available or ctrl + click to
 * additionally schedule autocrafting of what's craftable.</li>
 * <li><b>Some items are missing, some not craftable:</b> orange, same action as above.</li>
 * </ul>
 */
public class UseCraftingRecipeTransfer<T extends CraftingTermMenu>
        extends AbstractTransferHandler
        implements IRecipeTransferHandler<T, RecipeHolder<CraftingRecipe>> {

    private final Class<T> menuClass;
    private final MenuType<T> menuType;
    private final IRecipeTransferHandlerHelper helper;

    public UseCraftingRecipeTransfer(Class<T> menuClass, MenuType<T> menuType, IRecipeTransferHandlerHelper helper) {
        this.menuClass = menuClass;
        this.menuType = menuType;
        this.helper = helper;
    }

    @Override
    public IRecipeTransferError transferRecipe(T menu, RecipeHolder<CraftingRecipe> recipeHolder, IRecipeSlotsView display, Player player,
            boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();
        if (recipe.getType() != RecipeType.CRAFTING) {
            return helper.createInternalError();
        }

        if (recipe.getIngredients().isEmpty()) {
            return helper.createUserErrorWithTooltip(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        if (!recipe.canCraftInDimensions(CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT)) {
            return helper.createUserErrorWithTooltip(ItemModText.RECIPE_TOO_LARGE.text());
        }

        // Thank you RS for pioneering this amazing feature! :)
        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = display.getSlotViews(RecipeIngredientRole.INPUT);
        // Find missing ingredient
        var slotToIngredientMap = helper.getGuiSlotIndexToIngredientMap(recipeHolder);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        // Find missing ingredients and highlight the slots which have these
        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                // Highlight the slots with missing ingredients.
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new ErrorRenderer(missingSlots, craftMissing, color);
            }
        } else {
            CraftingHelper.performTransfer(menu, recipeHolder.id(), recipe, craftMissing);
        }

        // No error
        return null;
    }

    @Override
    public Class<T> getContainerClass() {
        return menuClass;
    }

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    private record ErrorRenderer(CraftingTermMenu.MissingIngredientSlots indices, boolean craftMissing,
            int color) implements IRecipeTransferError {
        @Override
        public Type getType() {
            return Type.COSMETIC;
        }

        @Override
        public int getButtonHighlightColor() {
            return color;
        }

        @Override
        public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX,
                int recipeY) {
            var poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(recipeX, recipeY, 0);

            // draw slot highlights
            var slotViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
            for (int i = 0; i < slotViews.size(); i++) {
                var slotView = slotViews.get(i);
                boolean missing = indices.missingSlots().contains(i);
                boolean craftable = indices.craftableSlots().contains(i);
                if (missing || craftable) {
                    slotView.drawHighlight(
                            guiGraphics,
                            missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                }
            }

            poseStack.popPose();
        }

        @Override
        public int getMissingCountHint() {
            return indices.missingSlots().size();
        }

        @Override
        public void getTooltip(ITooltipBuilder tooltip) {
            tooltip.addAll(TransferHelper.createCraftingTooltip(indices, craftMissing, true));
        }
    }
}
