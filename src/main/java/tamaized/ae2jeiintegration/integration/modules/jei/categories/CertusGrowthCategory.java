package tamaized.ae2jeiintegration.integration.modules.jei.categories;

import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.decorative.solid.BuddingCertusQuartzBlock;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.CyclingDrawable;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.LabelWidget;
import tamaized.ae2jeiintegration.integration.modules.jei.widgets.WidgetFactory;

import java.util.List;

/**
 * Displays information about growing quartz from {@link appeng.decorative.solid.BuddingCertusQuartzBlock}.
 */
public class CertusGrowthCategory extends ViewBasedCategory<CertusGrowthCategory.Page> {

    public static final RecipeType<Page> TYPE = RecipeType.create(AppEng.MOD_ID, "certus_growth", Page.class);

    private final List<ItemStack> BUDDING_QUARTZ_VARIANTS = List.of(
            AEBlocks.DAMAGED_BUDDING_QUARTZ.stack(),
            AEBlocks.CHIPPED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack());

    private final List<ItemStack> BUDDING_QUARTZ_DECAY_ORDER = List.of(
            AEBlocks.QUARTZ_BLOCK.stack(),
            AEBlocks.DAMAGED_BUDDING_QUARTZ.stack(),
            AEBlocks.CHIPPED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWED_BUDDING_QUARTZ.stack());

    private final List<ItemStack> BUD_GROWTH_STAGES = List.of(
            AEBlocks.SMALL_QUARTZ_BUD.stack(),
            AEBlocks.MEDIUM_QUARTZ_BUD.stack(),
            AEBlocks.LARGE_QUARTZ_BUD.stack(),
            AEBlocks.QUARTZ_CLUSTER.stack());

    private final int centerX;

    public CertusGrowthCategory(IGuiHelper guiHelper) {
        super(
            guiHelper,
            CyclingDrawable.forItems(
                guiHelper,
                AEBlocks.SMALL_QUARTZ_BUD,
                AEBlocks.MEDIUM_QUARTZ_BUD,
                AEBlocks.LARGE_QUARTZ_BUD,
                AEBlocks.QUARTZ_CLUSTER),
            ItemModText.CERTUS_QUARTZ_GROWTH.text(),
            guiHelper.createBlankDrawable(150, 60)
        );
        this.centerX = background.getWidth() / 2;
    }

    @Override
    public RecipeType<Page> getRecipeType() {
        return TYPE;
    }

    @Override
    protected View getView(Page page) {
        return switch (page) {
            case BUD_GROWTH -> new BudGrowthView();
            case BUD_LOOT, CLUSTER_LOOT -> new LootView(page);
            case BUDDING_QUARTZ_DECAY -> new BuddingQuartzDecayView();
            case BUDDING_QUARTZ_MOVING -> new BuddingQuartzMovingView();
            case GETTING_BUDDING_QUARTZ -> new GettingBuddingQuartzView();
            case FLAWLESS_BUDDING_QUARTZ -> new FlawlessBuddingQuartzView();
            case BUDDING_QUARTZ_ACCELERATION -> new BuddingQuartzAccelerationView();
        };
    }

    public enum Page {
        BUD_GROWTH,
        BUD_LOOT,
        CLUSTER_LOOT,
        BUDDING_QUARTZ_DECAY,
        BUDDING_QUARTZ_MOVING,
        GETTING_BUDDING_QUARTZ,
        FLAWLESS_BUDDING_QUARTZ,
        BUDDING_QUARTZ_ACCELERATION
    }

    /**
     * This page explains that buds grow on budding quartz.
     */
    private class BudGrowthView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text = ItemModText.QUARTZ_BUDS_GROW_ON_BUDDING_QUARTZ.text();
            builder.addWidget(new LabelWidget(centerX, 0, text)
                    .bodyText()
                    .maxWidth(background.getWidth()));

            builder.addWidget(WidgetFactory.unfilledArrow(guiHelper, centerX - 12, 25));
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addSlot(RecipeIngredientRole.CATALYST, centerX - 40, 25)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            builder.addSlot(RecipeIngredientRole.OUTPUT, centerX + 40 - 18, 25)
                    .setStandardSlotBackground()
                    .addItemStacks(BUD_GROWTH_STAGES);
        }
    }

    /**
     * These pages explain the loot for buds and crystal clusters, and that fortune is useful.
     */
    private class LootView implements View {
        private final Page page;

        public LootView(Page page) {
            this.page = page;
        }

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text;
            if (page == Page.BUD_LOOT) {
                text = ItemModText.BUDS_DROP_DUST_WHEN_NOT_FULLY_GROWN.text();
            } else {
                text = ItemModText.FULLY_GROWN_BUDS_DROP_CRYSTALS.text();
            }
            builder.addWidget(new LabelWidget(centerX, 0, text)
                    .bodyText()
                    .maxWidth(background.getWidth()));

            builder.addWidget(WidgetFactory.unfilledArrow(guiHelper, centerX - 12, 25));

            if (page == Page.CLUSTER_LOOT) {
                Component text1 = ItemModText.FORTUNE_APPLIES.text();
                builder.addWidget(new LabelWidget(centerX, 50, text1)
                        .bodyText());
            }
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            List<ItemStack> input;
            if (page == Page.BUD_LOOT) {
                input = List.of(
                        AEBlocks.SMALL_QUARTZ_BUD.stack(),
                        AEBlocks.MEDIUM_QUARTZ_BUD.stack(),
                        AEBlocks.LARGE_QUARTZ_BUD.stack());
            } else {
                input = List.of(
                        AEBlocks.QUARTZ_CLUSTER.stack());
            }

            builder.addSlot(RecipeIngredientRole.INPUT, centerX - 40, 25)
                    .setStandardSlotBackground()
                    .addItemStacks(input);

            ItemStack finalResult;
            if (page == Page.BUD_LOOT) {
                finalResult = AEItems.CERTUS_QUARTZ_DUST.stack();
            } else {
                finalResult = AEItems.CERTUS_QUARTZ_CRYSTAL.stack(4);
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, centerX + 40 - 18, 25)
                    .setStandardSlotBackground()
                    .addItemStack(finalResult);
        }
    }

    /**
     * This page explains that budding quartz decays when buds grow on it.
     */
    private class BuddingQuartzDecayView implements View {

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text1 = ItemModText.IMPERFECT_BUDDING_QUARTZ_DECAYS.text();
            builder.addWidget(new LabelWidget(centerX, 0, text1)
                    .bodyText()
                    .maxWidth(background.getWidth()));

            builder.addWidget(WidgetFactory.unfilledArrow(guiHelper, centerX - 12, 30));

            var decayChancePct = 100 / BuddingCertusQuartzBlock.DECAY_CHANCE;
            Component text = ItemModText.DECAY_CHANCE.text(decayChancePct);
            builder.addWidget(new LabelWidget(centerX, 50, text)
                    .bodyText());
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            var slot1 = builder.addSlot(RecipeIngredientRole.INPUT, centerX - 40, 30)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            var slot2 = builder.addSlot(RecipeIngredientRole.OUTPUT, centerX + 40 - 18, 30)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.createFocusLink(slot1, slot2);
        }
    }

    /**
     * This page explains how budding quartz can be moved.
     */
    private class BuddingQuartzMovingView implements View {
        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            var slot1 = builder.addSlot(RecipeIngredientRole.INPUT, centerX - 40, 0)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            var slot2 = builder.addSlot(RecipeIngredientRole.OUTPUT, centerX + 40 - 18, 0)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.createFocusLink(slot1, slot2);
        }

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            builder.addWidget(WidgetFactory.unfilledArrow(guiHelper, centerX - 12, 0));

            builder.addScrollBoxWidget(getWidth(), getHeight() - 20, 0, 20)
                .setContents(List.of(
                    ItemModText.BUDDING_QUARTZ_DECAYS_WHEN_BROKEN.text().withColor(0x7E7E7E),
                    ItemModText.SILK_TOUCH_PREVENTS_DECAY_FOR_IMPERFECT.text().withColor(0x7E7E7E),
                    ItemModText.SPATIAL_IO_NEVER_CAUSES_ANY_DECAY.text().withColor(0x7E7E7E)
                ));
        }
    }

    /**
     * This page explains how budding quartz can be made or be found.
     */
    private class GettingBuddingQuartzView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text = ItemModText.BUDDING_QUARTZ_CREATION_AND_WORLDGEN.text();
            builder.addWidget(new LabelWidget(22, 13, text)
                    .bodyText()
                    .alignLeft()
                    .maxWidth(background.getWidth() - 20));
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            // Also include quartz blocks in the list, since those can spawn in meteorites
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.addSlot(RecipeIngredientRole.INPUT, 1, 22)
                    .setStandardSlotBackground()
                    .addItemStack(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.stack());

            builder.addSlot(RecipeIngredientRole.CATALYST, 1, 43)
                    .setStandardSlotBackground()
                    .addItemStack(AEItems.METEORITE_COMPASS.stack());
        }
    }

    /**
     * This page explains what flawless budding quartz is.
     */
    private class FlawlessBuddingQuartzView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text = ItemModText.FLAWLESS_BUDDING_QUARTZ_DESCRIPTION.text();
            builder.addWidget(new LabelWidget(22, 13, text)
                    .bodyText()
                    .alignLeft()
                    .maxWidth(background.getWidth() - 20));
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 1, 13)
                    .setStandardSlotBackground()
                    .addItemStack(AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack());

            builder.addSlot(RecipeIngredientRole.INPUT, 1, 33)
                    .setStandardSlotBackground()
                    .addItemStack(AEItems.METEORITE_COMPASS.stack());
        }
    }

    /**
     * This page explains how budding quartz can be initially found.
     */
    private class BuddingQuartzAccelerationView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            var centerX = background.getWidth() / 2;

            Component text = ItemModText.CRYSTAL_GROWTH_ACCELERATORS_EFFECT.text();
            builder.addWidget(new LabelWidget(centerX, 0, text)
                    .bodyText()
                    .maxWidth(background.getWidth()));

            builder.addWidget(new LabelWidget(centerX, 45, Component.literal("+")));
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addSlot(RecipeIngredientRole.INPUT, centerX - 8 - 16, 40)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            builder.addSlot(RecipeIngredientRole.CATALYST, centerX + 8, 40)
                    .setStandardSlotBackground()
                    .addItemStack(AEBlocks.GROWTH_ACCELERATOR.stack());
        }
    }
}
