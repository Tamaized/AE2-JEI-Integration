package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.core.AppEng;
import appeng.items.parts.FacadeItem;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

/**
 * This plugin will dynamically add facade recipes for any item that can be turned into a facade.
 */
class FacadeRegistryPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>> {

    private final IVanillaRecipeFactory vanillaRecipeFactory;
    private final FacadeItem itemFacade;
    private final ItemStack cableAnchor;

    FacadeRegistryPlugin(IVanillaRecipeFactory vanillaRecipeFactory, FacadeItem itemFacade, ItemStack cableAnchor) {
        this.vanillaRecipeFactory = vanillaRecipeFactory;
        this.itemFacade = itemFacade;
        this.cableAnchor = cableAnchor;
    }

    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        var stackFocus = input.getItemStack().orElse(ItemStack.EMPTY);

        // Looking up if a certain block can be used to make a facade
		return !this.itemFacade.createFacadeForItem(stackFocus, true).isEmpty();
	}

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        var stackFocus = output.getItemStack().orElse(ItemStack.EMPTY);

        // Looking up how a certain facade is crafted
		return stackFocus.getItem() instanceof FacadeItem;
	}

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> input) {
        var focusStack = input.getItemStack().orElse(ItemStack.EMPTY);

        // Looking up if a certain block can be used to make a facade
        ItemStack facade = this.itemFacade.createFacadeForItem(focusStack, false);
        if (!facade.isEmpty()) {
            return List.of(make(focusStack, this.cableAnchor, facade));
        }
        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> output) {
        var focusStack = output.getItemStack().orElse(ItemStack.EMPTY);

        // Looking up how a certain facade is crafted
        if (focusStack.getItem() instanceof FacadeItem facadeItem) {
            ItemStack textureItem = facadeItem.getTextureItem(focusStack);
            return List.of(make(textureItem, this.cableAnchor, focusStack));
        }
        return List.of();
    }

    @Override
    public List<RecipeHolder<CraftingRecipe>> getAllRecipes() {
        // too many to show, don't try
        return List.of();
    }

    private RecipeHolder<CraftingRecipe> make(ItemStack textureItem, ItemStack cableAnchor, ItemStack result) {
        // This id should only be used within JEI and not really matter
        var itemId = BuiltInRegistries.ITEM.getKey(textureItem.getItem());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID,
                "facade/" + itemId.getNamespace() + "/" + itemId.getPath());

        var output = result.copyWithCount(4);

        CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, List.of(output))
            .define('a', Ingredient.of(cableAnchor))
            .define('i', Ingredient.of(textureItem))
            .pattern(" a ")
            .pattern("aia")
            .pattern(" a ")
            .build();

        return new RecipeHolder<>(id, recipe);
    }
}
