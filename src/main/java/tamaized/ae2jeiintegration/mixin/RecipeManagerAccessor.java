package tamaized.ae2jeiintegration.mixin;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Invoker
    <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> invokeByType(RecipeType<T> pRecipeType);
}
