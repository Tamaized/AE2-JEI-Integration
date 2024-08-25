package tamaized.ae2jeiintegration;

import appeng.init.InitRecipeTypes;
import appeng.recipes.entropy.EntropyRecipe;
import appeng.recipes.handlers.ChargerRecipe;
import appeng.recipes.handlers.InscriberRecipe;
import appeng.recipes.mattercannon.MatterCannonAmmo;
import appeng.recipes.transform.TransformRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.common.Mod;

@Mod(AE2JEIIntegration.MODID)
public class AE2JEIIntegration {

    public static final String MODID = "ae2jeiintegration";

    public AE2JEIIntegration() {
        hack_ensureRecipeTypesAreLoaded();
    }

    /**
	 * Hack workaround for <a href="https://github.com/AppliedEnergistics/Applied-Energistics-2/issues/8167">AE2 Issue #8167</a>
	 * <p>
     * AE2 will only register recipe types in {@link InitRecipeTypes#init(Registry)}.
	 * If a recipe type has not been accessed before that time, it will never get registered.
	 * This call is a workaround that ensures all recipes types get registered.
	 */
    @SuppressWarnings({"unused", "UnusedAssignment"})
	private void hack_ensureRecipeTypesAreLoaded() {
        RecipeType<?> hack = EntropyRecipe.TYPE;
        hack = ChargerRecipe.TYPE;
        hack = InscriberRecipe.TYPE;
        hack = MatterCannonAmmo.TYPE;
        hack = TransformRecipe.TYPE;
    }
}
