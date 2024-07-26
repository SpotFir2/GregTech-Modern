package com.gregtechceu.gtceu.common.machine.multiblock.generator;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
public class DysonSphere extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DysonSphere.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public DysonSphere(IMachineBlockEntity holder) {
        super(holder);
    }

    @Persisted
    private int DysonSphereData;
    @Persisted
    private int DysonSpheredamageData;

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean onWorking() {
        if (getOffsetTimer() % 10 == 0 && getDysonSphereData() > 0 && getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.setDayTime(18000L);
        }
        boolean value = super.onWorking();
        if (getRecipeLogic().getProgress() == 199 && getDysonSphereData() < 1000 &&
                getRecipeLogic().getDuration() == 200) {
            if (getDysonSpheredamageData() > 70) {
                this.DysonSpheredamageData = 0;
            } else {
                this.DysonSphereData++;
            }
        }
        if (getRecipeLogic().getDuration() == 20 && getRecipeLogic().getProgress() == 19 &&
                Math.random() < 0.01 * (1 + (double) getDysonSphereData() / 128) && getDysonSphereData() > 0) {
            if (getDysonSpheredamageData() > 99) {
                this.DysonSphereData--;
                this.DysonSpheredamageData = 0;
            } else {
                this.DysonSpheredamageData++;
            }
        }
        return value;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        final BlockPos pos = getPos();
        final Level level = getLevel();
        BlockPos[] coordinates = new BlockPos[] {
                pos.offset(4, 14, 0),
                pos.offset(-4, 14, 0),
                pos.offset(0, 14, 4),
                pos.offset(0, 14, -4) };
        for (BlockPos blockPos : coordinates) {
            if (Objects.equals(level.kjs$getBlock(blockPos).getId(), "kubejs:dyson_receiver_casing")) {
                for (int i = -6; i < 7; i++) {
                    for (int j = -6; j < 7; j++) {
                        if (i != 0 && j != 0 && level.kjs$getBlock(blockPos.offset(i, 1, j)).getSkyLight() == 0) {
                            getRecipeLogic().resetRecipeLogic();
                            return false;
                        }
                    }
                }
            }
        }
        if (recipe != null && RecipeHelper.getInputEUt(recipe) == GTValues.V[GTValues.UIV]) {
            return getDysonSphereData() < 1000;
        } else {
            return getDysonSphereData() > 0;
        }
    }

    private double getEfficiency() {
        return (double) (100 - Math.max(0, getDysonSpheredamageData() - 60)) / 100;
    }

    @Override
    public long getOverclockVoltage() {
        return (long) (GTValues.V[GTValues.MAX] * getDysonSphereData() * getEfficiency());
    }

    @Nullable
    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof DysonSphere engineMachine) {
            long eut = RecipeHelper.getOutputEUt(recipe);
            if (eut == GTValues.V[GTValues.MAX]) {
                eut = (long) (eut * engineMachine.getEfficiency());
                GTRecipe recipe1 = recipe.copy();
                recipe1.tickOutputs.put(EURecipeCapability.CAP, List.of(new Content(eut, 1.0f, 0.0f, null, null)));
                recipe1 = GTRecipeModifiers.fastParallel(engineMachine, recipe1,
                        (int) Math.max(1, engineMachine.getOverclockVoltage() / eut), false).getFirst();
                return recipe1;
            }
            return recipe;
        }
        return null;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!this.isFormed) return;
        textList.add(Component.literal("发射次数：" + getDysonSphereData() + " / 1000"));
        textList.add(Component.literal("设备损坏度：" + getDysonSpheredamageData() + " %"));
        textList.add(Component.literal("最大能量输出：").append(getDysonSphereData() > 0 ?
                Component.literal(getOverclockVoltage() + " EU/t") : Component.literal("0 EU/t")));
    }
}
