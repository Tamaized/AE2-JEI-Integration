package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.StackWithBounds;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class GenericEntryStackHelper {
    private GenericEntryStackHelper() {
    }

    @Nullable
    public static <T> GenericStack ingredientToStack(IIngredientType<T> type, T ingredient) {
        var converter = IngredientConverters.getConverter(type);
        if (converter != null) {
            return converter.getStackFromIngredient(ingredient);
        }
        return null;
    }

    @Nullable
    public static <T> GenericStack ingredientToStack(ITypedIngredient<T> ingredient) {
        return ingredientToStack(ingredient.getType(), ingredient.getIngredient());
    }

    @Nullable
    public static IClickableIngredient<?> stackToClickableIngredient(IIngredientManager manager, StackWithBounds stack) {
        for (var converter : IngredientConverters.getConverters()) {
            var ingredient = converter.getIngredientFromStack(stack.stack());
            if (ingredient != null) {
                var clickableIngredient = manager.createClickableIngredient(ingredient, stack.bounds(), true);
                if (clickableIngredient.isPresent()) {
                    return clickableIngredient.get();
                }
            }
        }

        return null;
    }

    public static List<List<GenericStack>> ofInputs(IRecipeSlotsView recipeLayout) {
        return recipeLayout.getSlotViews().stream()
                .filter(slotView -> slotView.getRole() == RecipeIngredientRole.INPUT)
                .map(GenericEntryStackHelper::ofSlot)
                .map(Stream::toList)
                .toList();
    }

    public static List<GenericStack> ofOutputs(IRecipeSlotsView recipeLayout) {
        return recipeLayout.getSlotViews().stream()
                .filter(slotView -> slotView.getRole() == RecipeIngredientRole.OUTPUT)
                .flatMap(slot -> ofSlot(slot).limit(1))
                .toList();
    }

    private static Stream<GenericStack> ofSlot(IRecipeSlotView slot) {
        return slot.getAllIngredients()
                .map(GenericEntryStackHelper::ingredientToStack)
                .filter(Objects::nonNull);
    }
}
