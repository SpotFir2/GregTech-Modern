package com.gregtechceu.gtceu.api.recipe;

import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.GTUtil;

import it.unimi.dsi.fastutil.longs.LongIntMutablePair;
import it.unimi.dsi.fastutil.longs.LongIntPair;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jetbrains.annotations.NotNull;

/**
 * A class for holding all the various Overclocking logics
 */
public class OverclockingLogic {

    @FunctionalInterface
    public interface Logic {

        /**
         * Calls the desired overclocking logic to be run for the recipe.
         * Performs the actual overclocking on the provided recipe.
         * Override this to call custom overclocking mechanics
         *
         * @param recipe     current recipe
         * @param recipeEUt  the EUt of the recipe
         * @param maxVoltage the maximum voltage the recipe is allowed to be run at
         * @param duration   the duration of the recipe
         * @param amountOC   the maximum amount of overclocks to perform
         * @return an int array of {OverclockedEUt, OverclockedDuration}
         */
        LongIntPair runOverclockingLogic(@NotNull GTRecipe recipe, long recipeEUt, long maxVoltage, int duration,
                                         int amountOC);
    }

    public static final double STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER = 4.0;
    public static final double STANDARD_OVERCLOCK_DURATION_DIVISOR = ConfigHolder.INSTANCE.machines.overclockDivisor;
    public static final double PERFECT_OVERCLOCK_DURATION_DIVISOR = 4.0;

    public static final OverclockingLogic PERFECT_OVERCLOCK = new OverclockingLogic(PERFECT_OVERCLOCK_DURATION_DIVISOR,
            STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER);
    public static final OverclockingLogic NON_PERFECT_OVERCLOCK = new OverclockingLogic(
            STANDARD_OVERCLOCK_DURATION_DIVISOR, STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER);

    @Getter
    protected Logic logic;

    public OverclockingLogic(Logic logic) {
        this.logic = logic;
    }

    public OverclockingLogic(double durationDivisor, double voltageMultiplier) {
        this.logic = (recipe, recipeEUt, maxVoltage, duration, amountOC) -> standardOverclockingLogic(
                Math.abs(recipeEUt),
                maxVoltage,
                duration,
                amountOC,
                durationDivisor,
                voltageMultiplier);
    }

    /**
     * applies standard logic for overclocking, where each overclock modifies energy and duration
     *
     * @param recipeEUt         the EU/t of the recipe to overclock
     * @param maxVoltage        the maximum voltage the recipe is allowed to be run at
     * @param recipeDuration    the duration of the recipe to overclock
     * @param durationDivisor   the value to divide the duration by for each overclock
     * @param voltageMultiplier the value to multiply the voltage by for each overclock
     * @param numberOfOCs       the maximum amount of overclocks allowed
     * @return an int array of {OverclockedEUt, OverclockedDuration}
     */
    @NotNull
    public static LongIntPair standardOverclockingLogic(long recipeEUt, long maxVoltage, int recipeDuration,
                                                        int numberOfOCs, double durationDivisor,
                                                        double voltageMultiplier) {
        double resultDuration = recipeDuration;
        double resultVoltage = recipeEUt;

        for (; numberOfOCs > 0; numberOfOCs--) {
            // it is important to do voltage first,
            // so overclocking voltage does not go above the limit before changing duration

            double potentialVoltage = resultVoltage * voltageMultiplier;
            // do not allow voltage to go above maximum
            if (potentialVoltage > maxVoltage) break;

            double potentialDuration = resultDuration / durationDivisor;
            // do not allow duration to go below one tick
            if (potentialDuration < 1) break;
            // update the duration for the next iteration
            resultDuration = potentialDuration;

            // update the voltage for the next iteration after everything else
            // in case duration overclocking would waste energy
            resultVoltage = potentialVoltage;
        }
        return LongIntMutablePair.of((long) resultVoltage, (int) resultDuration);
    }

    /**
     * applies standard logic for overclocking, where each overclock modifies energy and duration
     *
     * @param recipeEUt         the EU/t of the recipe to overclock
     * @param maxVoltage        the maximum voltage the recipe is allowed to be run at
     * @param recipeDuration    the duration of the recipe to overclock
     * @param durationDivisor   the value to divide the duration by for each overclock
     * @param voltageMultiplier the value to multiply the voltage by for each overclock
     * @param numberOfOCs       the maximum amount of overclocks allowed
     * @return an int array of {OverclockedEUt, OverclockedDuration, PotentialParallels}
     */
    @NotNull
    public static ImmutableTriple<Long, Integer, Integer> standardOverclockingLogicWithSubTickParallelCount(long recipeEUt,
                                                                                                            long maxVoltage,
                                                                                                            int recipeDuration,
                                                                                                            int numberOfOCs,
                                                                                                            double durationDivisor,
                                                                                                            double voltageMultiplier) {
        double resultDuration = recipeDuration;
        double resultVoltage = recipeEUt;
        double resultParallel = 1;

        for (; numberOfOCs > 0; numberOfOCs--) {
            // it is important to do voltage first,
            // so overclocking voltage does not go above the limit before changing duration

            double potentialVoltage = resultVoltage * voltageMultiplier;
            // do not allow voltage to go above maximum
            if (potentialVoltage > maxVoltage) break;

            double potentialDuration = resultDuration / durationDivisor;

            if (potentialDuration < 1) {
                resultParallel *= durationDivisor;
                if (potentialDuration > (double) 1. / durationDivisor) {
                    potentialDuration *= durationDivisor;
                }
            }

            // update the duration for the next iteration
            resultDuration = Math.max(1, potentialDuration);

            // update the voltage for the next iteration after everything else
            // in case duration overclocking would waste energy
            resultVoltage = potentialVoltage;
        }
        return ImmutableTriple.of((long) resultVoltage, (int) resultDuration, (int) resultParallel);
    }

    @NotNull
    public static LongIntPair heatingCoilOverclockingLogic(long recipeEUt, long maximumVoltage, int recipeDuration,
                                                           int maxOverclocks, int currentTemp, int recipeRequiredTemp) {
        int amountEUDiscount = Math.max(0, (currentTemp - recipeRequiredTemp) / 900);
        double reductionDuration = Math.max(0.5, (double) recipeRequiredTemp / currentTemp);
        // apply a multiplicative 95% energy multiplier for every 900k over recipe temperature
        recipeEUt *= Math.min(1, Math.pow(0.95, amountEUDiscount));

        return standardOverclockingLogic(recipeEUt, maximumVoltage, (int) (reductionDuration * recipeDuration),
                maxOverclocks, 4, 4);
    }

    /**
     * Finds the maximum tier that a recipe can overclock to, when provided the maximum voltage a recipe can overclock
     * to.
     *
     * @param voltage The maximum voltage the recipe is allowed to overclock to.
     * @return the highest voltage tier the machine should use to overclock with
     */
    protected int getOverclockForTier(long voltage) {
        return GTUtil.getTierByVoltage(voltage);
    }
}
