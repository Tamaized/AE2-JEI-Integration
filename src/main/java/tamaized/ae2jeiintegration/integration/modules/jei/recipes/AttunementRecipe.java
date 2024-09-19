package tamaized.ae2jeiintegration.integration.modules.jei.recipes;

import appeng.api.features.P2PTunnelAttunementInternal;
import appeng.core.localization.ItemModText;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record AttunementRecipe(
	Ingredient inputs,
	Item tunnel,
	@Nullable ResourceLocation uid,
	Component... description
) {
	public static List<AttunementRecipe> createAllRecipes() {
		List<AttunementRecipe> results = new ArrayList<>();
		for (var entry : P2PTunnelAttunementInternal.getApiTunnels()) {
			var attunementRecipe = createAttunementDisplay(entry);
			if (!attunementRecipe.inputs().isEmpty()) {
				results.add(attunementRecipe);
			}
		}

		for (var entry : P2PTunnelAttunementInternal.getTagTunnels().entrySet()) {
			var tagKey = entry.getKey();
			var tunnelType = entry.getValue();
			var attunementRecipe = createAttunementDisplay(tunnelType, tagKey);
			if (!attunementRecipe.inputs().isEmpty()) {
				results.add(attunementRecipe);
			}
		}
		return results;
	}

	private static AttunementRecipe createAttunementDisplay(P2PTunnelAttunementInternal.Resultant entry) {
		var tunnelType = entry.tunnelType();
		var inputs = BuiltInRegistries.ITEM.stream()
			.map(ItemStack::new)
			.filter(entry.stackPredicate());
		return new AttunementRecipe(
			Ingredient.of(inputs),
			tunnelType,
			getUid(tunnelType),
			ItemModText.P2P_API_ATTUNEMENT.text(),
			entry.description());
	}

	private static AttunementRecipe createAttunementDisplay(Item tunnelType, TagKey<Item> tagKey) {
		return new AttunementRecipe(
			Ingredient.of(tagKey),
			tunnelType,
			getUid(tunnelType),
			ItemModText.P2P_TAG_ATTUNEMENT.text());
	}

	private static @Nullable ResourceLocation getUid(Item tunnelType) {
		@SuppressWarnings("deprecation")
		var attunementKey = tunnelType.builtInRegistryHolder().getKey();
		return attunementKey == null ? null : attunementKey.location();
	}

}
