package tamaized.ae2jeiintegration.integration.modules.jei.subtypes;

import appeng.api.ids.AEComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FacadeSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final FacadeSubtypeInterpreter INSTANCE = new FacadeSubtypeInterpreter();

	private FacadeSubtypeInterpreter() {}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return ingredient.get(AEComponents.FACADE_ITEM);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		var facadeItem = ingredient.get(AEComponents.FACADE_ITEM);
		if (facadeItem == null) {
			return "";
		}
		return facadeItem.value().builtInRegistryHolder().key().location().toString();
	}
}
