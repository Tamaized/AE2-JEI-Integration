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
import tamaized.ae2jeiintegration.integration.modules.jei.Colors;
import tamaized.ae2jeiintegration.integration.modules.jei.drawables.CyclingDrawable;

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
            TYPE,
            ItemModText.CERTUS_QUARTZ_GROWTH.text(),
            CyclingDrawable.forItems(
                guiHelper,
                AEBlocks.SMALL_QUARTZ_BUD,
                AEBlocks.MEDIUM_QUARTZ_BUD,
                AEBlocks.LARGE_QUARTZ_BUD,
                AEBlocks.QUARTZ_CLUSTER),
            150, 
            60
        );
        this.centerX = 150 / 2;
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
            builder.addText(text, 0, 0, getWidth(), 20)
                .alignHorizontalCenter()
                .setColor(Colors.BODY);
            
            builder.addRecipeArrow(centerX - 12, 25);
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addSlot(RecipeIngredientRole.CATALYST, centerX - 40, 25)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            builder.addOutputSlot(centerX + 40 - 18, 25)
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

            builder.addText(text, 0, 0, getWidth(), 20)
                .alignHorizontalCenter()
                .setColor(Colors.BODY);

            builder.addRecipeArrow(centerX - 12, 25);

            if (page == Page.CLUSTER_LOOT) {
                builder.addText(ItemModText.FORTUNE_APPLIES.text(), 0, 50, getWidth(), 10)
                    .alignHorizontalCenter()
                    .setColor(Colors.BODY);
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

            builder.addInputSlot(centerX - 40, 25)
                    .setStandardSlotBackground()
                    .addItemStacks(input);

            ItemStack finalResult;
            if (page == Page.BUD_LOOT) {
                finalResult = AEItems.CERTUS_QUARTZ_DUST.stack();
            } else {
                finalResult = AEItems.CERTUS_QUARTZ_CRYSTAL.stack(4);
            }
            builder.addOutputSlot(centerX + 40 - 18, 25)
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
            builder.addText(ItemModText.IMPERFECT_BUDDING_QUARTZ_DECAYS.text(), 0, 0, getWidth(), 20)
                    .setColor(Colors.BODY)
                    .alignHorizontalCenter();

            builder.addRecipeArrow(centerX - 12, 30);

            var decayChancePct = 100 / BuddingCertusQuartzBlock.DECAY_CHANCE;
            Component text = ItemModText.DECAY_CHANCE.text(decayChancePct);
            builder.addText(text, 0, 50, getWidth(), 10)
                    .setColor(Colors.BODY)
                    .alignHorizontalCenter();
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            var input = builder.addInputSlot(centerX - 40, 30)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            var output = builder.addOutputSlot(centerX + 40 - 18, 30)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.createFocusLink(input, output);
        }
    }

    /**
     * This page explains how budding quartz can be moved.
     */
    private class BuddingQuartzMovingView implements View {
        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            var input = builder.addInputSlot(centerX - 40, 0)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            var output = builder.addOutputSlot(centerX + 40 - 18, 0)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.createFocusLink(input, output);
        }

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            builder.addRecipeArrow(centerX - 12, 0);

            builder.addScrollBoxWidget(getWidth(), getHeight() - 20, 0, 20)
                .setContents(List.of(
                    ItemModText.BUDDING_QUARTZ_DECAYS_WHEN_BROKEN.text().withColor(Colors.BODY),
                    ItemModText.SILK_TOUCH_PREVENTS_DECAY_FOR_IMPERFECT.text().withColor(Colors.BODY),
                    ItemModText.SPATIAL_IO_NEVER_CAUSES_ANY_DECAY.text().withColor(Colors.BODY)
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
            builder.addText(text, 22, 0, getWidth() - 22, getHeight())
                .alignVerticalCenter()
                .setColor(Colors.BODY);
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            // Also include quartz blocks in the list, since those can spawn in meteorites
            builder.addInputSlot(1, 1)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_DECAY_ORDER);

            builder.addInputSlot(1, 22)
                    .setStandardSlotBackground()
                    .addItemLike(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED);

            builder.addSlot(RecipeIngredientRole.CATALYST, 1, 43)
                    .setStandardSlotBackground()
                    .addItemLike(AEItems.METEORITE_COMPASS);
        }
    }

    /**
     * This page explains what flawless budding quartz is.
     */
    private class FlawlessBuddingQuartzView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text = ItemModText.FLAWLESS_BUDDING_QUARTZ_DESCRIPTION.text();
            builder.addText(text, 22, 0, getWidth() - 22, getHeight())
                .alignVerticalCenter()
                .setColor(Colors.BODY);
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 1, 15)
                    .setStandardSlotBackground()
                    .addItemLike(AEBlocks.FLAWLESS_BUDDING_QUARTZ);

            builder.addInputSlot(1, 35)
                    .setStandardSlotBackground()
                    .addItemLike(AEItems.METEORITE_COMPASS);
        }
    }

    /**
     * This page explains how budding quartz can be initially found.
     */
    private class BuddingQuartzAccelerationView implements View {
        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, IFocusGroup focuses) {
            Component text = ItemModText.CRYSTAL_GROWTH_ACCELERATORS_EFFECT.text();
            builder.addText(text, 0, 0, getWidth(), 38)
                    .alignHorizontalCenter()
                    .setLineSpacing(0)
                    .setColor(Colors.BODY);

            builder.addText(Component.literal("+"), centerX - 8, 40, 16, 16)
                .alignHorizontalCenter()
                .alignVerticalCenter()
                .setColor(0xFFFFFFFF)
                .setShadow(true);
        }

        @Override
        public void buildSlots(IRecipeLayoutBuilder builder) {
            builder.addInputSlot(centerX - 8 - 16, 40)
                    .setStandardSlotBackground()
                    .addItemStacks(BUDDING_QUARTZ_VARIANTS);

            builder.addSlot(RecipeIngredientRole.CATALYST, centerX + 8, 40)
                    .setStandardSlotBackground()
                    .addItemLike(AEBlocks.GROWTH_ACCELERATOR);
        }
    }
}
