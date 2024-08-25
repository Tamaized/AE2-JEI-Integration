package tamaized.ae2jeiintegration.integration.modules.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public record AttunementDisplay(
	Ingredient inputs,
	Item tunnel,
	@Nullable ResourceLocation uid,
	Component... description
) {
}
