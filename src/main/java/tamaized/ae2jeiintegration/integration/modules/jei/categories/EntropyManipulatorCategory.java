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
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
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
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.Label;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

public class EntropyManipulatorCategory implements IRecipeCategory<RecipeHolder<EntropyRecipe>> {
    public static final RecipeType<RecipeHolder<EntropyRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.ENTROPY);

    private final WidgetFactory widgetFactory;
    private final IDrawable slotBackground;
    private final IDrawable background;
    private final IDrawable icon;
    private final IPlatformFluidHelper<?> fluidHelper;
    private final IDrawable blockDestroyOverlay;
    private final IDrawable iconHeat;
    private final IDrawable iconCool;
    private final int centerX;

    public EntropyManipulatorCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.slotBackground = guiHelper.getSlotDrawable();
        this.background = guiHelper.createBlankDrawable(130, 50);
        this.fluidHelper = helpers.getPlatformFluidHelper();
        // We don't use an item drawable here because it would show the charge bar
        this.icon = guiHelper.drawableBuilder(
                AppEng.makeId("textures/item/entropy_manipulator.png"),
                0,
                0,
                16,
                16).setTextureSize(16, 16).build();
        this.blockDestroyOverlay = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 52, 16, 16);
        this.iconHeat = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 68, 6, 6);
        this.iconCool = guiHelper.createDrawable(JEIPlugin.TEXTURE, 6, 68, 6, 6);
        this.centerX = background.getWidth() / 2;
        this.widgetFactory = new WidgetFactory(helpers);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        EntropyRecipe recipe = holder.value();
        var input = builder.addSlot(RecipeIngredientRole.INPUT, centerX - 36, 15)
            .setBackground(slotBackground, -1, -1);
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
                .setBackground(slotBackground, -1, -1);
            setFluidOrBlockSlot(destroyed, inputBlock, inputFluid);
            destroyed.setOverlay(blockDestroyOverlay, 0, 0);
            destroyed.addRichTooltipCallback((recipeSlotView, tooltip) ->
                tooltip.add(ItemModText.CONSUMED.text().withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
            );
            x += 18;
        } else if (outputBlock != null || outputFluid != null) {
            var output = builder.addSlot(RecipeIngredientRole.OUTPUT, x, 15)
                .setBackground(slotBackground, -1, -1);
            setFluidOrBlockSlot(output, outputBlock, outputFluid);
            x += 18;
        }

        for (var drop : recipe.getDrops()) {
            var output = builder.addSlot(RecipeIngredientRole.OUTPUT, x, 15)
                .setBackground(slotBackground, -1, -1);
            output.addItemStack(drop);
            x += 18;
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        EntropyRecipe recipe = holder.value();

        var icon = switch (recipe.getMode()) {
            case HEAT -> iconHeat;
            case COOL -> iconCool;
        };
        var labelText = switch (recipe.getMode()) {
            case HEAT -> ItemModText.ENTROPY_MANIPULATOR_HEAT.text(EntropyManipulatorItem.ENERGY_PER_USE);
            case COOL -> ItemModText.ENTROPY_MANIPULATOR_COOL.text(EntropyManipulatorItem.ENERGY_PER_USE);
        };
        var interaction = switch (recipe.getMode()) {
            case HEAT -> ItemModText.RIGHT_CLICK.text();
            case COOL -> ItemModText.SHIFT_RIGHT_CLICK.text();
        };

        Label modeLabel = widgetFactory.label(centerX + 4, 2, labelText)
            .bodyText();
        builder.addWidget(modeLabel);
        builder.addWidget(widgetFactory.drawable(modeLabel.getBounds().getX() - 9, 3, icon));
        builder.addWidget(widgetFactory.unfilledArrow(centerX - 12, 14));
        builder.addWidget(widgetFactory.label(centerX, 38, interaction).bodyText());
    }

    private void setFluidOrBlockSlot(IRecipeSlotBuilder slot, @Nullable Block block, @Nullable Fluid fluid) {
        if (fluid != null) {
            // The volume does assume BUCKET == BLOCK in terms of volume. But most of the time this should be true.

            // On Fabric, we cannot add fluid variants for flowing fluids so rendering would fail.
            // But we need to tell the player that they need to use the manipulator on the *flowing* variant
            // anyway, so this if-block would be needed in any case.
            if (!fluid.isSource(fluid.defaultFluidState())) {
                if (fluid instanceof FlowingFluid flowingFluid) {
                    slot.addFluidStack(flowingFluid.getSource(), fluidHelper.bucketVolume());
                } else {
                    // Don't really know how to get the source :-(
                    slot.addFluidStack(fluid, fluidHelper.bucketVolume());
                    AELog.warn("Don't know how to get the source fluid for %s", fluid);
                }
            } else {
                slot.addFluidStack(fluid, fluidHelper.bucketVolume());
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
    public Component getTitle() {
        return AEItems.ENTROPY_MANIPULATOR.asItem().getDescription();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<EntropyRecipe> holder) {
        return holder.id();
    }
}
