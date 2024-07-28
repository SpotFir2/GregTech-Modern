package com.gregtechceu.gtceu.common.machine.multiblock.steam;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.steam.SteamEnergyRecipeHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.machine.multiblock.part.SteamHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamParallelMultiblockMachine extends WorkableMultiblockMachine implements IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SteamParallelMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    private final int max_parallels;

    private boolean isOC;

    @Persisted
    private int amountOC;

    // if in millibuckets, this is 0.5, Meaning 2mb of steam -> 1 EU
    private static final double CONVERSION_RATE = FluidHelper.getBucket() * 2 / 1000.0D;

    public SteamParallelMultiblockMachine(IMachineBlockEntity holder, int maxParallels, Object... args) {
        super(holder, args);
        max_parallels = maxParallels;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var handlers = capabilitiesProxy.get(IO.IN, FluidRecipeCapability.CAP);
        if (handlers == null) return;
        var itr = handlers.iterator();
        while (itr.hasNext()) {
            var handler = itr.next();
            if (handler instanceof NotifiableFluidTank tank &&
                    tank.getMachine() instanceof SteamHatchPartMachine steamHatchPart) {
                this.isOC = steamHatchPart.getDefinition() == GTMachines.LARGE_STEAM_HATCH;
                itr.remove();
                if (!capabilitiesProxy.contains(IO.IN, EURecipeCapability.CAP)) {
                    capabilitiesProxy.put(IO.IN, EURecipeCapability.CAP, new ArrayList<>());
                }
                Objects.requireNonNull(capabilitiesProxy.get(IO.IN, EURecipeCapability.CAP))
                        .add(new SteamEnergyRecipeHandler(tank,
                                CONVERSION_RATE * (this.isOC ? Math.pow(3, this.amountOC) : 1)));
                return;
            }
        }
    }

    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe,
                                          double reductionDuration) {
        if (machine instanceof SteamParallelMultiblockMachine steamMachine) {
            if (RecipeHelper.getInputEUt(recipe) > (steamMachine.isOC ? 128 : 32)) {
                return null;
            }
            var result = GTRecipeModifiers.accurateParallel(machine, recipe, steamMachine.max_parallels, false)
                    .getFirst();
            recipe = result == recipe ? result.copy() : result;
            recipe.duration = (int) Math.max(1, recipe.duration * reductionDuration /
                    (steamMachine.isOC ? Math.pow(2, steamMachine.amountOC) : 1));
        }
        return recipe;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);
        if (isFormed()) {
            var handlers = capabilitiesProxy.get(IO.IN, EURecipeCapability.CAP);
            if (handlers != null && !handlers.isEmpty() &&
                    handlers.get(0) instanceof SteamEnergyRecipeHandler steamHandler) {
                if (steamHandler.getCapacity() > 0) {
                    long steamStored = steamHandler.getStored();
                    textList.add(Component.translatable("gtceu.multiblock.steam.steam_stored", steamStored,
                            steamHandler.getCapacity()));
                }
            }

            if (!isWorkingEnabled()) {
                textList.add(Component.translatable("gtceu.multiblock.work_paused"));

            } else if (isActive()) {
                textList.add(Component.translatable("gtceu.multiblock.running"));
                int currentProgress = (int) (recipeLogic.getProgressPercent() * 100);
                textList.add(Component.translatable("gtceu.multiblock.parallel", this.max_parallels));
                textList.add(Component.translatable("gtceu.multiblock.progress", currentProgress));
            } else {
                textList.add(Component.translatable("gtceu.multiblock.idling"));
            }

            if (recipeLogic.isWaiting()) {
                textList.add(Component.translatable("gtceu.multiblock.steam.low_steam")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }

            if (this.isOC) {
                textList.add(Component.literal("超频次数：" + this.amountOC)
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("每次超频将减少2倍耗时和增加3倍蒸汽消耗")))));

                textList.add(Component.literal("修改超频次数：")
                        .append(ComponentPanelWidget.withButton(Component.literal("[-] "), "ocSub"))
                        .append(ComponentPanelWidget.withButton(Component.literal("[+]"), "ocAdd")));
            }
        }
    }

    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote && this.isOC) {
            this.amountOC = Mth.clamp(this.amountOC + (componentData.equals("ocAdd") ? 1 : -1), 0, 4);
        }
    }

    @Override
    public IGuiTexture getScreenTexture() {
        return GuiTextures.DISPLAY_STEAM.get(ConfigHolder.INSTANCE.machines.steelSteamMultiblocks);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        var screen = new DraggableScrollableWidgetGroup(7, 4, 162, 121).setBackground(getScreenTexture());
        screen.addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()));
        screen.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .setMaxWidthLimit(150)
                .clickHandler(this::handleDisplayClick));
        return new ModularUI(176, 216, this, entityPlayer)
                .background(GuiTextures.BACKGROUND_STEAM.get(ConfigHolder.INSTANCE.machines.steelSteamMultiblocks))
                .widget(screen)
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(),
                        GuiTextures.SLOT_STEAM.get(ConfigHolder.INSTANCE.machines.steelSteamMultiblocks), 7, 134,
                        true));
    }
}
