package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.handlers.InscriberRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class InscriberRecipeCategory implements IRecipeCategory<RecipeHolder<InscriberRecipe>> {

    private static final String TITLE_TRANSLATION_KEY = "block.ae2.inscriber";

    public static final RecipeType<RecipeHolder<InscriberRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(InscriberRecipe.TYPE);

    private final IDrawable background;

    private final IDrawableAnimated progress;

    private final IDrawable icon;

    public InscriberRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "textures/guis/inscriber.png");
        this.background = guiHelper.createDrawable(location, 36, 20, 105, 54);

        IDrawableStatic progressDrawable = guiHelper.drawableBuilder(location, 177, 0, 6, 18).addPadding(19, 0, 100, 0)
                .build();
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM,
                false);

        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, AEBlocks.INSCRIBER.stack());
    }

    @Override
    public RecipeType<RecipeHolder<InscriberRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE_TRANSLATION_KEY);
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InscriberRecipe> holder, IFocusGroup focuses) {
        InscriberRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .setSlotName("top")
                .addIngredients(recipe.getTopOptional());
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 19)
                .setSlotName("middle")
                .addIngredients(recipe.getMiddleInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 3, 35)
                .setSlotName("bottom")
                .addIngredients(recipe.getBottomOptional());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 20)
                .setSlotName("output")
                .addItemStack(recipe.getResultItem());
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(RecipeHolder<InscriberRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        this.progress.draw(guiGraphics);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<InscriberRecipe> holder) {
        return holder.id();
    }
}
