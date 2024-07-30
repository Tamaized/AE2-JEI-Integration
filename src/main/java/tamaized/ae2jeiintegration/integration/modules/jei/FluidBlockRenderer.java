package tamaized.ae2jeiintegration.integration.modules.jei;

import java.util.List;

import appeng.integration.modules.itemlists.FluidBlockRendering;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredientRenderer;

public class FluidBlockRenderer implements IIngredientRenderer<FluidStack> {
    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient) {
        var fluid = ingredient.getFluid();
        FluidBlockRendering.render(guiGraphics, fluid, 0, 0, 16, 16);
    }

    @Override
    public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag) {
        // JEI adds the mod name automatically
        return List.of(ingredient.getDisplayName());
    }
}
