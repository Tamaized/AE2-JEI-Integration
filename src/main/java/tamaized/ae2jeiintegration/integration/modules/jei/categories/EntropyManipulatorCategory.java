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
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
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
import tamaized.ae2jeiintegration.integration.modules.jei.Colors;
import tamaized.ae2jeiintegration.integration.modules.jei.JEIPlugin;

public class EntropyManipulatorCategory extends AbstractRecipeCategory<RecipeHolder<EntropyRecipe>> {
    public static final RecipeType<RecipeHolder<EntropyRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(AERecipeTypes.ENTROPY);
    private static final int RECIPE_WIDTH = 130;

    private final IDrawable blockDestroyOverlay;
    private final int centerX;
    private final ModeExtension heatExtension;
    private final ModeExtension coolExtension;

    public EntropyManipulatorCategory(IGuiHelper guiHelper) {
        super(
            RECIPE_TYPE,
            AEItems.ENTROPY_MANIPULATOR.asItem().getDescription(),
            // We don't use an item drawable here because it would show the charge bar
            guiHelper.drawableBuilder(
                AppEng.makeId("textures/item/entropy_manipulator.png"),
                0,
                0,
                16,
                16).setTextureSize(16, 16).build(),

            RECIPE_WIDTH,
            50
        );
        this.blockDestroyOverlay = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 52, 16, 16);
        var iconHeat = guiHelper.createDrawable(JEIPlugin.TEXTURE, 0, 68, 6, 6);
        var iconCool = guiHelper.createDrawable(JEIPlugin.TEXTURE, 6, 68, 6, 6);
        this.centerX = RECIPE_WIDTH / 2;

        this.heatExtension = new ModeExtension(
            iconHeat,
            ItemModText.ENTROPY_MANIPULATOR_HEAT.text(EntropyManipulatorItem.ENERGY_PER_USE),
            ItemModText.RIGHT_CLICK,
            RECIPE_WIDTH
        );
        this.coolExtension = new ModeExtension(
            iconCool,
            ItemModText.ENTROPY_MANIPULATOR_COOL.text(EntropyManipulatorItem.ENERGY_PER_USE),
            ItemModText.SHIFT_RIGHT_CLICK,
            RECIPE_WIDTH
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        var input = builder.addInputSlot(centerX - 36, 15)
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
            var output = builder.addOutputSlot(x, 15)
                .setStandardSlotBackground();
            setFluidOrBlockSlot(output, outputBlock, outputFluid);
            x += 18;
        }

        for (var drop : recipe.getDrops()) {
            var output = builder.addOutputSlot(x, 15)
                .setStandardSlotBackground();
            output.addItemStack(drop);
            x += 18;
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<EntropyRecipe> holder, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
        var recipe = holder.value();

        var extension = switch (recipe.getMode()) {
            case HEAT -> heatExtension;
            case COOL -> coolExtension;
        };

        extension.createRecipeExtras(builder);
    }

    private static class ModeExtension {
        private final IDrawableStatic icon;
        private final int width;
        private final Component modeText;
        private final ItemModText interaction;
        private final int centerX;

        public ModeExtension(IDrawableStatic icon, Component modeText, ItemModText interaction, int width) {
            this.modeText = modeText;
            this.interaction = interaction;
            this.centerX = width / 2;
            this.icon = icon;
            this.width = width;
        }

        public void createRecipeExtras(IRecipeExtrasBuilder builder) {
            builder.addRecipeArrow(centerX - 12, 14);

            int modeLabelX = centerX - 36;
            builder.addText(modeText, modeLabelX, 0, width - modeLabelX, 10)
                .setColor(Colors.BODY);

            int iconX = modeLabelX - 9;
            builder.addDrawable(icon, iconX, 1);

            builder.addText(interaction.text(), 0, 38, width, 10)
                .alignHorizontalCenter()
                .setColor(Colors.BODY);
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
            slot.addItemLike(block);
        }
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<EntropyRecipe> holder) {
        return holder.id();
    }
}
