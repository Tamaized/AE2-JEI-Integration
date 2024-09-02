package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.StackWithBounds;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;

import java.util.List;
import java.util.Objects;

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
            var ingredient = makeClickableIngredient(manager, converter, stack);
            if (ingredient != null) {
                return ingredient;
            }
        }

        return null;
    }

    @Nullable
    private static <T> IClickableIngredient<T> makeClickableIngredient(IIngredientManager manager,
                                                                   IngredientConverter<T> converter, StackWithBounds stack) {
        var ingredient = converter.getIngredientFromStack(stack.stack());
		if (ingredient == null) {
			return null;
		}
        return manager.getIngredientTypeChecked(ingredient)
            .flatMap(type -> {
                IIngredientHelper<T> ingredientHelper = manager.getIngredientHelper(type);
                T normalized = ingredientHelper.normalizeIngredient(ingredient);
                return manager.createTypedIngredient(type, normalized)
                    .map(typedIngredient -> new ClickableIngredient<>(normalized, type, typedIngredient, stack.bounds()));
            })
            .orElse(null);
	}

    public static List<List<GenericStack>> ofInputs(IRecipeSlotsView recipeLayout) {
        return recipeLayout.getSlotViews(RecipeIngredientRole.INPUT)
                .stream()
                .map(GenericEntryStackHelper::ofSlot)
                .toList();
    }

    public static List<GenericStack> ofOutputs(IRecipeSlotsView recipeLayout) {
        return recipeLayout.getSlotViews(RecipeIngredientRole.OUTPUT)
                .stream()
                .flatMap(slot -> ofSlot(slot).stream().limit(1))
                .toList();
    }

    private static List<GenericStack> ofSlot(IRecipeSlotView slot) {
        return slot.getAllIngredients()
                .map(GenericEntryStackHelper::ingredientToStack)
                .filter(Objects::nonNull)
                .toList();
    }

    private record ClickableIngredient<T> (
        T ingredient,
        IIngredientType<T> ingredientType,
        ITypedIngredient<T> typedIngredient,
        Rect2i area
    ) implements IClickableIngredient<T> {
        @SuppressWarnings("removal")
        @Override
        public ITypedIngredient<T> getTypedIngredient() {
            return typedIngredient;
        }

        @Override
        public IIngredientType<T> getIngredientType() {
            return ingredientType;
        }

        @Override
        public T getIngredient() {
            return ingredient;
        }

        @Override
        public Rect2i getArea() {
            return area;
        }
    }
}
