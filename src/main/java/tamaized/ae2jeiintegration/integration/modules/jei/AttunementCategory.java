package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ItemModText;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.View;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Widget;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

import java.util.List;

public class AttunementCategory extends ViewBasedCategory<AttunementDisplay> {

    public static final RecipeType<AttunementDisplay> TYPE = RecipeType.create(AppEng.MOD_ID, "attunement",
            AttunementDisplay.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;

    public AttunementCategory(IJeiHelpers helpers) {
        super(helpers);
        var guiHelpers = helpers.getGuiHelper();
        this.background = guiHelpers.createBlankDrawable(130, 36);
        this.icon = guiHelpers.createDrawableItemStack(AEParts.ME_P2P_TUNNEL.stack());
        this.slotBackground = guiHelpers.getSlotDrawable();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public Component getTitle() {
        return ItemModText.P2P_TUNNEL_ATTUNEMENT.text();
    }

    @Override
    public RecipeType<AttunementDisplay> getRecipeType() {
        return TYPE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    protected View getView(AttunementDisplay recipe) {
        var x = background.getWidth() / 2 - 41;
        var y = background.getHeight() / 2 - 13;

        return new View() {
            @Override
            public void buildSlots(IRecipeLayoutBuilder builder) {
                builder.addSlot(RecipeIngredientRole.INPUT, x + 4, y + 5)
                        .setBackground(slotBackground, -1, -1)
                        .addIngredients(recipe.inputs())
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            for (Component line : recipe.description()) {
                                tooltip.add(line);
                            }
                        });
                builder.addSlot(RecipeIngredientRole.OUTPUT, x + 61, y + 5)
                        .setBackground(slotBackground, -1, -1)
                        .addItemStack(new ItemStack(recipe.tunnel()));
            }

            @Override
            public void createWidgets(WidgetFactory factory, List<Widget> widgets) {
                widgets.add(factory.unfilledArrow(x + 27, y + 4));
            }
        };
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(AttunementDisplay recipe) {
        return recipe.uid();
    }
}
