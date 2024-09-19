package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;

public abstract class ViewBasedCategory<T> extends AbstractCategory<T> {

    protected ViewBasedCategory(IGuiHelper guiHelper, IDrawable icon, Component title, IDrawable background) {
        super(guiHelper, icon, title, background);
    }

    protected abstract View getView(T recipe);

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        View view = getView(recipe);
        view.buildSlots(builder);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        var view = getView(recipe);
        view.createRecipeExtras(builder, focuses);
    }

    public interface View {
        default void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {

        }

        default void buildSlots(IRecipeLayoutBuilder builder) {
        }
    }
}
