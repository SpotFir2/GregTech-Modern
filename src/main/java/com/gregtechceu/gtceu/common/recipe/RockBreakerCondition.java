package com.gregtechceu.gtceu.common.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2023/3/15
 * @implNote RockBreakerCondition
 */
@NoArgsConstructor
public class RockBreakerCondition extends RecipeCondition {

    public final static RockBreakerCondition INSTANCE = new RockBreakerCondition();

    @Override
    public String getType() {
        return "rock_breaker";
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("recipe.condition.rock_breaker.tooltip");
    }

    @Override
    public boolean test(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        var fluidA = BuiltInRegistries.FLUID.get(new ResourceLocation(recipe.data.getString("fluidA")));
        var fluidB = BuiltInRegistries.FLUID.get(new ResourceLocation(recipe.data.getString("fluidB")));
        boolean hasFluidA = false, hasFluidB = false;
        if (recipeLogic.machine instanceof WorkableElectricMultiblockMachine MMachine) {
            var handlers = MMachine.getCapabilitiesProxy().get(IO.IN, FluidRecipeCapability.CAP);
            if (handlers == null) return false;
            for (com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler<?> handler : handlers) {
                if (handler instanceof NotifiableFluidTank tank) {
                    if (tank.getFluidInTank(0).getFluid() == fluidA) hasFluidA = true;
                    if (tank.getFluidInTank(0).getFluid() == fluidB) hasFluidB = true;
                    if (hasFluidA && hasFluidB) return true;
                }
            }
        } else {
            var level = recipeLogic.machine.self().getLevel();
            var pos = recipeLogic.machine.self().getPos();
            for (Direction side : GTUtil.DIRECTIONS) {
                if (side.getAxis() != Direction.Axis.Y) {
                    var fluid = level.getFluidState(pos.relative(side));
                    if (fluid.getType() == fluidA) hasFluidA = true;
                    if (fluid.getType() == fluidB) hasFluidB = true;
                    if (hasFluidA && hasFluidB) return true;
                }
            }
        }
        return false;
    }

    @Override
    public RecipeCondition createTemplate() {
        return new RockBreakerCondition();
    }
}
