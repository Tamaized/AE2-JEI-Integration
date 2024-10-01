package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.api.config.CondenserOutput;
import appeng.api.implementations.items.IStorageComponent;
import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.client.gui.Icon;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.ButtonToolTips;
import appeng.items.materials.StorageComponentItem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.DrawableIcon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CondenserCategory extends AbstractRecipeCategory<CondenserOutput> {

    private static final String TITLE_TRANSLATION_KEY = "block.ae2.condenser";
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "textures/guis/condenser.png");

    public static final RecipeType<CondenserOutput> RECIPE_TYPE = RecipeType.create(AppEng.MOD_ID, "condenser",
            CondenserOutput.class);

    private final IDrawableAnimated progress;
    private final IDrawable backgroundTrash;
    private final IDrawable toolbarButtonBackground;

    private final Map<CondenserOutput, IDrawable> buttonIcons;
    private final Map<CondenserOutput, ResourceLocation> resourceLocations;
    private final IDrawable background;

    public CondenserCategory(IGuiHelper guiHelper) {
        super(
            RECIPE_TYPE,
            Component.translatable(TITLE_TRANSLATION_KEY),
            guiHelper.createDrawableItemLike(AEBlocks.CONDENSER),
            96,
            48
        );
        this.background = guiHelper.createDrawable(TEXTURE, 48, 25, 96, 48);
        this.progress = guiHelper.drawableBuilder(TEXTURE, 176, 0, 6, 18)
                .addPadding(0, 0, 72, 0)
                .buildAnimated(40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.backgroundTrash = new DrawableIcon(Icon.BACKGROUND_TRASH);
        this.toolbarButtonBackground = new DrawableIcon(Icon.TOOLBAR_BUTTON_BACKGROUND);

        this.buttonIcons = new EnumMap<>(CondenserOutput.class);
        this.buttonIcons.put(CondenserOutput.MATTER_BALLS, new DrawableIcon(Icon.CONDENSER_OUTPUT_MATTER_BALL));
        this.buttonIcons.put(CondenserOutput.SINGULARITY, new DrawableIcon(Icon.CONDENSER_OUTPUT_SINGULARITY));

        this.resourceLocations = new EnumMap<>(CondenserOutput.class);
        this.resourceLocations.put(CondenserOutput.TRASH, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "trash"));
        this.resourceLocations.put(CondenserOutput.MATTER_BALLS, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "matter_balls"));
        this.resourceLocations.put(CondenserOutput.SINGULARITY, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "singularity"));
    }

    private static ItemStack getOutput(CondenserOutput recipe) {
        return switch (recipe) {
            case MATTER_BALLS -> AEItems.MATTER_BALL.stack();
            case SINGULARITY -> AEItems.SINGULARITY.stack();
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void draw(CondenserOutput recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics);

        // This is shown on the "input slot" for condenser operations to indicate that any item can be used
        this.backgroundTrash.draw(guiGraphics, 3, 27);

        this.toolbarButtonBackground.draw(guiGraphics, 80, 26);

        var buttonIcon = this.buttonIcons.get(recipe);
        if (buttonIcon != null) {
            buttonIcon.draw(guiGraphics, 81, 27);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserOutput recipe, IFocusGroup focuses) {
        builder.addOutputSlot(57, 27)
                .addItemStack(getOutput(recipe));

        // Get all storage cells and cycle them through a catalyst slot
        builder.addSlot(RecipeIngredientRole.CATALYST, 53, 1)
                .addItemStacks(getViableStorageComponents(recipe));
    }

    private static List<ItemStack> getViableStorageComponents(CondenserOutput condenserOutput) {
        List<ItemStack> viableComponents = new ArrayList<>();
        addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_1K);
        addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_4K);
        addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_16K);
        addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_64K);
        addViableComponent(condenserOutput, viableComponents, AEItems.CELL_COMPONENT_256K);
        return viableComponents;
    }

    private static void addViableComponent(CondenserOutput condenserOutput, List<ItemStack> viableComponents,
                                           ItemDefinition<StorageComponentItem> storageComponent) {
        IStorageComponent comp = storageComponent.get();
        ItemStack itemStack = storageComponent.stack();
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

    @Override
    public @Nullable ResourceLocation getRegistryName(CondenserOutput recipe) {
        return this.resourceLocations.get(recipe);
    }
}
