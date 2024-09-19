package tamaized.ae2jeiintegration.integration.modules.jei;

import appeng.api.config.CondenserOutput;
import appeng.client.gui.AEBaseScreen;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.FacadeCreativeTab;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.localization.GuiText;
import appeng.core.localization.ItemModText;
import appeng.integration.abstraction.ItemListMod;
import appeng.integration.abstraction.ItemListModAdapter;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.me.items.WirelessCraftingTermMenu;
import appeng.recipes.AERecipeTypes;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.AttunementCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.CertusGrowthCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.ChargerCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.CondenserCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.EntropyManipulatorCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.InscriberRecipeCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.TransformCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.converters.FluidIngredientConverter;
import tamaized.ae2jeiintegration.integration.modules.jei.converters.ItemIngredientConverter;
import tamaized.ae2jeiintegration.integration.modules.jei.recipes.AttunementRecipe;
import tamaized.ae2jeiintegration.integration.modules.jei.subtypes.FacadeSubtypeInterpreter;
import tamaized.ae2jeiintegration.integration.modules.jei.transfer.EncodePatternTransferHandler;
import tamaized.ae2jeiintegration.integration.modules.jei.transfer.UseCraftingRecipeTransfer;

import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/jei.png");

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "core");

    public JEIPlugin() {
        IngredientConverters.register(new ItemIngredientConverter());
        IngredientConverters.register(new FluidIngredientConverter());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(AEItems.FACADE.asItem(), FacadeSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var jeiHelpers = registry.getJeiHelpers();
        var guiHelper = jeiHelpers.getGuiHelper();
        registry.addRecipeCategories(
                new TransformCategory(guiHelper),
                new CondenserCategory(guiHelper),
                new InscriberRecipeCategory(guiHelper),
                new ChargerCategory(guiHelper),
                new AttunementCategory(guiHelper),
                new CertusGrowthCategory(guiHelper),
                new EntropyManipulatorCategory(guiHelper));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        var jeiHelpers = registration.getJeiHelpers();
        var ingredientVisibility = jeiHelpers.getIngredientVisibility();
        var transferHelper = registration.getTransferHelper();

        // Allow vanilla crafting recipe transfer from JEI to crafting terminal
        registration.addRecipeTransferHandler(
                new UseCraftingRecipeTransfer<>(CraftingTermMenu.class, CraftingTermMenu.TYPE, transferHelper),
                RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(
                new UseCraftingRecipeTransfer<>(WirelessCraftingTermMenu.class, WirelessCraftingTermMenu.TYPE,
                    transferHelper),
                RecipeTypes.CRAFTING);

        // Universal handler for processing to try and handle all IRecipe
        registration.addUniversalRecipeTransferHandler(new EncodePatternTransferHandler<>(
                PatternEncodingTermMenu.TYPE,
                PatternEncodingTermMenu.class,
                transferHelper,
                ingredientVisibility
        ));

        if (ModList.get().isLoaded("ae2wtlib")) {
            registration.addRecipeTransferHandler(
                    new UseCraftingRecipeTransfer<>(WCTMenu.class, WCTMenu.TYPE, transferHelper),
                    RecipeTypes.CRAFTING);
            registration.addUniversalRecipeTransferHandler(
                    new EncodePatternTransferHandler<>(WETMenu.TYPE, WETMenu.class, transferHelper,
                        ingredientVisibility));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        assert level != null;
        var recipeManager = level.getRecipeManager();

        registration.addRecipes(InscriberRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(AERecipeTypes.INSCRIBER));
        registration.addRecipes(ChargerCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(AERecipeTypes.CHARGER));
        registration.addRecipes(CondenserCategory.RECIPE_TYPE, List.of(CondenserOutput.MATTER_BALLS, CondenserOutput.SINGULARITY));
        registration.addRecipes(EntropyManipulatorCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(AERecipeTypes.ENTROPY));
        registration.addRecipes(TransformCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(AERecipeTypes.TRANSFORM));
        registration.addRecipes(AttunementCategory.RECIPE_TYPE, AttunementRecipe.createAllRecipes());
        registration.addRecipes(CertusGrowthCategory.TYPE, List.of(CertusGrowthCategory.Page.values()));

        var presses = List.of(
            AEItems.LOGIC_PROCESSOR_PRESS.stack(),
            AEItems.CALCULATION_PROCESSOR_PRESS.stack(),
            AEItems.ENGINEERING_PROCESSOR_PRESS.stack(),
            AEItems.SILICON_PRESS.stack()
        );
        registration.addItemStackInfo(presses, GuiText.inWorldCraftingPresses.text());
        registration.addIngredientInfo(AEBlocks.CRANK, ItemModText.CRANK_DESCRIPTION.text());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEBlocks.CONDENSER, CondenserCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(AEBlocks.INSCRIBER, InscriberRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(AEParts.CRAFTING_TERMINAL, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(AEItems.WIRELESS_CRAFTING_TERMINAL, RecipeTypes.CRAFTING);

        // Both the charger and crank will be used as catalysts here to make it more discoverable
        registration.addRecipeCatalyst(AEBlocks.CHARGER, ChargerCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(AEBlocks.CRANK, ChargerCategory.RECIPE_TYPE);

        registration.addRecipeCatalyst(AEItems.ENTROPY_MANIPULATOR, EntropyManipulatorCategory.RECIPE_TYPE);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        if (AEConfig.instance().isEnableFacadeRecipesInRecipeViewer()) {
            var vanillaRecipeFactory = registration.getJeiHelpers().getVanillaRecipeFactory();
            var itemFacade = AEItems.FACADE.asItem();
            var cableAnchor = AEParts.CABLE_ANCHOR.stack();
            var facadeRegistryPlugin = new FacadeRegistryPlugin(vanillaRecipeFactory, itemFacade, cableAnchor);
            registration.addTypedRecipeManagerPlugin(RecipeTypes.CRAFTING, facadeRegistryPlugin);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        var ingredientManager = registration.getJeiHelpers().getIngredientManager();
        registration.addGenericGuiContainerHandler(AEBaseScreen.class, new AEGuiContainerHandler(ingredientManager));

        @SuppressWarnings("unchecked")
        var aeBaseScreenClass = (Class<AEBaseScreen<?>>) (Object) AEBaseScreen.class;
        registration.addGhostIngredientHandler(aeBaseScreenClass, new GhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        var adapter = new JeiRuntimeAdapter(jeiRuntime);
        ItemListMod.setAdapter(adapter);

        var ingredientManager = jeiRuntime.getIngredientManager();
        var config = AEConfig.instance();

        if (!config.isDebugToolsEnabled()) {
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, getDebugTools());
        }

        if (!config.isEnableFacadesInRecipeViewer()) {
            ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                    FacadeCreativeTab.getDisplayItems());
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        ItemListMod.setAdapter(ItemListModAdapter.none());
    }

    private static Collection<ItemStack> getDebugTools() {
        // We use the internal API here as exception, because debug tools are not part of the public API by design.

        return List.of(
            AEBlocks.DEBUG_CUBE_GEN.stack(),
            AEBlocks.DEBUG_ENERGY_GEN.stack(),
            AEBlocks.DEBUG_ITEM_GEN.stack(),
            AEBlocks.DEBUG_PHANTOM_NODE.stack(),

            AEItems.DEBUG_CARD.stack(),
            AEItems.DEBUG_ERASER.stack(),
            AEItems.DEBUG_METEORITE_PLACER.stack(),
            AEItems.DEBUG_REPLICATOR_CARD.stack()
        );
    }

}
