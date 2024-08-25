package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.api.config.CondenserOutput;
import appeng.api.implementations.items.IStorageComponent;
import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.client.gui.Icon;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class CondenserCategory implements IRecipeCategory<CondenserOutput> {

    private static final String TITLE_TRANSLATION_KEY = "block.ae2.condenser";

    public static final RecipeType<CondenserOutput> RECIPE_TYPE = RecipeType.create(AppEng.MOD_ID, "condenser",
            CondenserOutput.class);

    private final IDrawable background;
    private final IDrawableAnimated progress;

    private final IDrawable iconButton;
    private final IDrawable iconTrash;
    private final IDrawable icon;

    private final Map<CondenserOutput, IDrawable> buttonIcons;

    public CondenserCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, AEBlocks.CONDENSER.stack());

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "textures/guis/condenser.png");
        this.background = guiHelper.createDrawable(location, 48, 25, 96, 48);

        // This is shown on the "input slot" for condenser operations to indicate that any item can be used
        this.iconTrash = new IconDrawable(Icon.BACKGROUND_TRASH, 3, 27);
        this.iconButton = new IconDrawable(Icon.TOOLBAR_BUTTON_BACKGROUND, 80, 26);

        IDrawableStatic progressDrawable = guiHelper.drawableBuilder(location, 176, 0, 6, 18).addPadding(0, 0, 72, 0)
                .build();
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM,
                false);

        this.buttonIcons = new EnumMap<>(CondenserOutput.class);

        this.buttonIcons.put(CondenserOutput.MATTER_BALLS,
                new IconDrawable(Icon.CONDENSER_OUTPUT_MATTER_BALL, 81, 27));
        this.buttonIcons.put(CondenserOutput.SINGULARITY,
                new IconDrawable(Icon.CONDENSER_OUTPUT_SINGULARITY, 81, 27));
    }

    private ItemStack getOutput(CondenserOutput recipe) {
        return switch (recipe) {
            case MATTER_BALLS -> AEItems.MATTER_BALL.stack();
            case SINGULARITY -> AEItems.SINGULARITY.stack();
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public RecipeType<CondenserOutput> getRecipeType() {
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
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CondenserOutput recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        this.progress.draw(guiGraphics);
        this.iconTrash.draw(guiGraphics);
        this.iconButton.draw(guiGraphics);

        var buttonIcon = this.buttonIcons.get(recipe);
        if (buttonIcon != null) {
            buttonIcon.draw(guiGraphics);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserOutput recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 27)
                .setSlotName("output")
                .addItemStack(getOutput(recipe));

        // Get all storage cells and cycle them through a catalyst slot
        builder.addSlot(RecipeIngredientRole.CATALYST, 53, 1)
                .setSlotName("storage_cell")
                .addItemStacks(getViableStorageComponents(recipe));
    }

    private List<ItemStack> getViableStorageComponents(CondenserOutput condenserOutput) {
        List<ItemStack> viableComponents = new ArrayList<>();
        this.addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_1K.stack());
        this.addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_4K.stack());
        this.addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_16K.stack());
        this.addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_64K.stack());
        this.addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_256K.stack());
        return viableComponents;
    }

    private void addViableComponent(CondenserOutput condenserOutput, List<ItemStack> viableComponents,
            ItemStack itemStack) {
        IStorageComponent comp = (IStorageComponent) itemStack.getItem();
        int storage = comp.getBytes(itemStack) * CondenserBlockEntity.BYTE_MULTIPLIER;
        if (storage >= condenserOutput.requiredPower) {
            viableComponents.add(itemStack);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CondenserOutput recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 80 && mouseX < 80 + 16 && mouseY >= 26 && mouseY < 26 + 16) {
            String key;

            switch (recipe) {
                case MATTER_BALLS:
                    key = ButtonToolTips.MatterBalls.getTranslationKey();
                    break;
                case SINGULARITY:
                    key = ButtonToolTips.Singularity.getTranslationKey();
                    break;
                default:
                    return;
            }

            String tooltipString = Component.translatable(key, recipe.requiredPower).getString();
            String[] tooltipLines = tooltipString.split("\n");
            for (String line : tooltipLines) {
                tooltip.add(Component.literal(line));
            }
        }
    }

}
