package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AELog;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.items.tools.powered.EntropyManipulatorItem;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.entropy.EntropyRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.JEIPlugin;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableHelper;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.LabelDrawable;

public class EntropyManipulatorCategory extends AbstractCategory<RecipeHolder<EntropyRecipe>> {
    public static final RecipeType<RecipeHolder<EntropyRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.ENTROPY);

    private final IDrawable blockDestroyOverlay;
    private final int centerX;
    private final IRecipeWidget heatWidget;
    private final IRecipeWidget coolWidget;

    public EntropyManipulatorCategory(IGuiHelper guiHelper) {
        super(
            guiHelper,
            // We don't use an item drawable here because it would show the charge bar
            guiHelper.drawableBuilder(
                AppEng.makeId("textures/item/entropy_manipulator.png"),
                0,
                0,
                16,
                16).setTextureSize(16, 16).build(),
            AEItems.ENTROPY_MANIPULATOR.asItem().getDescription(),
            guiHelper.createBlankDrawable(130, 50)
        );
        this.blockDestroyOverlay = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 52, 16, 16);
        var iconHeat = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 68, 6, 6);
        var iconCool = guiHelper.createDrawable(JEIPlugin.TEXTURE, 6, 68, 6, 6);
        this.centerX = background.getWidth() / 2;

        this.heatWidget = new ModeWidget(
            guiHelper,
            iconHeat,
            ItemModText.ENTROPY_MANIPULATOR_HEAT.text(EntropyManipulatorItem.ENERGY_PER_USE),
            ItemModText.RIGHT_CLICK,
            centerX
        );
        this.coolWidget = new ModeWidget(
            guiHelper,
            iconCool,
            ItemModText.ENTROPY_MANIPULATOR_COOL.text(EntropyManipulatorItem.ENERGY_PER_USE),
            ItemModText.SHIFT_RIGHT_CLICK,
            centerX
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        var input = builder.addSlot(RecipeIngredientRole.INPUT, centerX - 36, 15)
            .setStandardSlotBackground();
        var inputBlock = recipe.getInput().block().map(EntropyRecipe.BlockInput::block).orElse(null);
        var inputFluid = recipe.getInput().fluid().map(EntropyRecipe.FluidInput::fluid).orElse(null);
        setFluidOrBlockSlot(input, inputBlock, inputFluid);

        int x = centerX + 20;

        // TODO clean this up to use the optionals properly
        var outputBlock = recipe.getOutput().block().map(EntropyRecipe.BlockOutput::block).orElse(null);
        var outputFluid = recipe.getOutput().fluid().map(EntropyRecipe.FluidOutput::fluid).orElse(null);

        if (outputBlock == Blocks.AIR
            && (outputFluid == null || outputFluid == Fluids.EMPTY)) {
            // If the recipe destroys the block and produces no fluid in return,
            // show the input again, but overlay it with an X.
            var destroyed = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, 15)
                .setStandardSlotBackground();
            setFluidOrBlockSlot(destroyed, inputBlock, inputFluid);
            destroyed.setOverlay(blockDestroyOverlay, 0, 0);
            destroyed.addRichTooltipCallback((recipeSlotView, tooltip) ->
                tooltip.add(ItemModText.CONSUMED.text().withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
            );
            x += 18;
        } else if (outputBlock != null || outputFluid != null) {
            var output = builder.addSlot(RecipeIngredientRole.OUTPUT, x, 15)
                .setStandardSlotBackground();
            setFluidOrBlockSlot(output, outputBlock, outputFluid);
            x += 18;
        }

        for (var drop : recipe.getDrops()) {
            var output = builder.addSlot(RecipeIngredientRole.OUTPUT, x, 15)
                .setStandardSlotBackground();
            output.addItemStack(drop);
            x += 18;
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();

        var widget = switch (recipe.getMode()) {
            case HEAT -> heatWidget;
            case COOL -> coolWidget;
        };

        builder.addWidget(widget);
    }

    private static class ModeWidget implements IRecipeWidget {
        private final ScreenPosition position;
        private final IDrawableStatic icon;
        private final LabelDrawable modeLabel;
        private final IDrawable interactionText;
        private final IDrawableStatic unfilledArrow;
        private final int centerX;

        public ModeWidget(IGuiHelper guiHelper, IDrawableStatic icon, Component modeText, ItemModText interaction, int centerX) {
            this.centerX = centerX;
            this.position = new ScreenPosition(0, 0);
            this.icon = icon;
            this.modeLabel = new LabelDrawable(modeText).bodyText();
            this.interactionText = new LabelDrawable(interaction.text()).bodyText();
            this.unfilledArrow = DrawableHelper.getUnfilledArrow(guiHelper);
        }

        @Override
        public ScreenPosition getPosition() {
            return position;
        }

        @Override
        public void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
            int modeLabelCenterX = centerX + 4;
            modeLabel.draw(guiGraphics, modeLabelCenterX, 2);

            int iconX = modeLabelCenterX + modeLabel.getX() - 9;
            icon.draw(guiGraphics, iconX, 3);

            unfilledArrow.draw(guiGraphics, centerX - (unfilledArrow.getWidth() / 2), 14);
            interactionText.draw(guiGraphics, centerX, 38);
        }
    }

    private void setFluidOrBlockSlot(IRecipeSlotBuilder slot, @Nullable Block block, @Nullable Fluid fluid) {
        if (fluid != null) {
            // The volume does assume BUCKET == BLOCK in terms of volume. But most of the time this should be true.

            // On Fabric, we cannot add fluid variants for flowing fluids so rendering would fail.
            // But we need to tell the player that they need to use the manipulator on the *flowing* variant
            // anyway, so this if-block would be needed in any case.
            if (!fluid.isSource(fluid.defaultFluidState())) {
                if (fluid instanceof FlowingFluid flowingFluid) {
                    slot.addFluidStack(flowingFluid.getSource());
                } else {
                    // Don't really know how to get the source :-(
                    slot.addFluidStack(fluid);
                    AELog.warn("Don't know how to get the source fluid for %s", fluid);
                }
            } else {
                slot.addFluidStack(fluid);
            }
        } else if (block != null) {
            slot.addItemStack(block.asItem().getDefaultInstance());
        }
    }

    @Override
    public RecipeType<RecipeHolder<EntropyRecipe>> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<EntropyRecipe> holder) {
        return holder.id();
    }
}
