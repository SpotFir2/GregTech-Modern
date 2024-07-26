package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author KilaBash
 * @date 2023/7/11
 * @implNote GTBedrockFluids
 */
public class GTBedrockFluids {

    public static final Map<ResourceLocation, BedrockFluidDefinition> toReRegister = new HashMap<>();

    //////////////////////////////////////
    // ******** OVERWORLD ********//
    //////////////////////////////////////
    public static BedrockFluidDefinition HEAVY_OIL = BedrockFluidDefinition.builder(GTCEu.id("heavy_oil_deposit"))
            .fluid(GTMaterials.OilHeavy::getFluid)
            .weight(15)
            .yield(100, 200)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(20)
            .biomes(5, BiomeTags.IS_OCEAN)
            .biomes(10, CustomTags.IS_SANDY)
            .dimensions(overworld())
            .register();

    public static BedrockFluidDefinition LIGHT_OIL = BedrockFluidDefinition.builder(GTCEu.id("light_oil_deposit"))
            .fluid(GTMaterials.OilLight::getFluid)
            .weight(25)
            .yield(175, 300)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(25)
            .dimensions(overworld())
            .register();

    public static BedrockFluidDefinition NATURAL_GAS = BedrockFluidDefinition.builder(GTCEu.id("natural_gas_deposit"))
            .fluid(GTMaterials.NaturalGas::getFluid)
            .weight(15)
            .yield(100, 175)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(20)
            .dimensions(overworld())
            .register();

    public static BedrockFluidDefinition OIL = BedrockFluidDefinition.builder(GTCEu.id("oil_deposit"))
            .fluid(GTMaterials.Oil::getFluid)
            .weight(20)
            .yield(175, 300)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(25)
            .biomes(5, BiomeTags.IS_OCEAN)
            .biomes(5, CustomTags.IS_SANDY)
            .dimensions(overworld())
            .register();

    public static BedrockFluidDefinition RAW_OIL = BedrockFluidDefinition.builder(GTCEu.id("raw_oil_deposit"))
            .fluid(GTMaterials.RawOil::getFluid)
            .weight(20)
            .yield(200, 300)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(25)
            .dimensions(overworld())
            .register();

    public static BedrockFluidDefinition SALT_WATER = BedrockFluidDefinition.builder(GTCEu.id("salt_water_deposit"))
            .fluid(GTMaterials.SaltWater::getFluid)
            .weight(0)
            .yield(50, 100)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(15)
            .dimensions(overworld())
            .biomes(200, Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN)
            .biomes(150, BiomeTags.IS_OCEAN)
            .register();

    //////////////////////////////////////
    // ******** NETHER ********//
    //////////////////////////////////////
    public static BedrockFluidDefinition LAVA = BedrockFluidDefinition.builder(GTCEu.id("lava_deposit"))
            .fluid(GTMaterials.Lava::getFluid)
            .weight(65)
            .yield(125, 250)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(30)
            .dimensions(nether())
            .register();

    public static BedrockFluidDefinition NETHER_NATURAL_GAS = BedrockFluidDefinition
            .builder(GTCEu.id("nether_natural_gas_deposit"))
            .fluid(GTMaterials.NaturalGas::getFluid)
            .weight(35)
            .yield(150, 300)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(nether())
            .register();

    public static BedrockFluidDefinition HELIUM_3 = BedrockFluidDefinition
            .builder(GTCEu.id("helium3_deposit"))
            .fluid(GTMaterials.Helium3::getFluid)
            .weight(20)
            .yield(50, 80)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("ad_astra", "moon"))
            .register();

    public static BedrockFluidDefinition HELIUM = BedrockFluidDefinition
            .builder(GTCEu.id("helium_deposit"))
            .fluid(GTMaterials.Helium::getFluid)
            .weight(20)
            .yield(50, 100)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("ad_astra", "moon"))
            .register();

    public static BedrockFluidDefinition SULFURIC_ACID = BedrockFluidDefinition
            .builder(GTCEu.id("sulfuric_acid_deposit"))
            .fluid(GTMaterials.SulfuricAcid::getFluid)
            .weight(20)
            .yield(50, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("ad_astra", "venus"))
            .register();

    public static BedrockFluidDefinition DEUTERIUM = BedrockFluidDefinition
            .builder(GTCEu.id("deuterium_deposit"))
            .fluid(GTMaterials.Deuterium::getFluid)
            .weight(20)
            .yield(50, 100)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("ad_astra", "mercury"))
            .register();

    public static BedrockFluidDefinition RADON = BedrockFluidDefinition
            .builder(GTCEu.id("radon_deposit"))
            .fluid(GTMaterials.Radon::getFluid)
            .weight(20)
            .yield(50, 80)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("ad_astra", "mars"))
            .register();

    public static BedrockFluidDefinition CERES_RADON = BedrockFluidDefinition
            .builder(GTCEu.id("ceres_radon_deposit"))
            .fluid(GTMaterials.Radon::getFluid)
            .weight(20)
            .yield(50, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "ceres"))
            .register();

    public static BedrockFluidDefinition METHANE = BedrockFluidDefinition
            .builder(GTCEu.id("methane_deposit"))
            .fluid(GTMaterials.Methane::getFluid)
            .weight(20)
            .yield(50, 100)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "titan"))
            .register();

    public static BedrockFluidDefinition BENZENE = BedrockFluidDefinition
            .builder(GTCEu.id("benzene_deposit"))
            .fluid(GTMaterials.Benzene::getFluid)
            .weight(15)
            .yield(50, 80)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "titan"))
            .register();

    public static BedrockFluidDefinition CHARCOAL_BYPRODUCTS = BedrockFluidDefinition
            .builder(GTCEu.id("charcoal_byproducts"))
            .fluid(GTMaterials.CharcoalByproducts::getFluid)
            .weight(10)
            .yield(50, 60)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "titan"))
            .register();

    public static BedrockFluidDefinition COAL_GAS = BedrockFluidDefinition
            .builder(GTCEu.id("coal_gas_deposit"))
            .fluid(GTMaterials.CoalGas::getFluid)
            .weight(20)
            .yield(50, 200)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "io"))
            .register();

    public static BedrockFluidDefinition NITRIC_ACID = BedrockFluidDefinition
            .builder(GTCEu.id("nitric_acid_deposit"))
            .fluid(GTMaterials.NitricAcid::getFluid)
            .weight(20)
            .yield(80, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "pluto"))
            .register();

    public static BedrockFluidDefinition HYDROCHLORIC_ACID = BedrockFluidDefinition
            .builder(GTCEu.id("hydrochloric_acid_deposit"))
            .fluid(GTMaterials.HydrochloricAcid::getFluid)
            .weight(20)
            .yield(100, 200)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "ganymede"))
            .register();

    public static BedrockFluidDefinition CERES_XENON = BedrockFluidDefinition
            .builder(GTCEu.id("ceres_xenon_deposit"))
            .fluid(GTMaterials.Xenon::getFluid)
            .weight(20)
            .yield(50, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "ceres"))
            .register();

    public static BedrockFluidDefinition CERES_KRYPTON = BedrockFluidDefinition
            .builder(GTCEu.id("ceres_krypton_deposit"))
            .fluid(GTMaterials.Krypton::getFluid)
            .weight(20)
            .yield(50, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "ceres"))
            .register();

    public static BedrockFluidDefinition CERES_NEON = BedrockFluidDefinition
            .builder(GTCEu.id("ceres_neon_deposit"))
            .fluid(GTMaterials.Neon::getFluid)
            .weight(20)
            .yield(50, 150)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "ceres"))
            .register();

    public static BedrockFluidDefinition FLUORINE = BedrockFluidDefinition
            .builder(GTCEu.id("fluorine"))
            .fluid(GTMaterials.Fluorine::getFluid)
            .weight(10)
            .yield(80, 120)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "enceladus"))
            .register();

    public static BedrockFluidDefinition CHLORINE = BedrockFluidDefinition
            .builder(GTCEu.id("chlorine"))
            .fluid(GTMaterials.Chlorine::getFluid)
            .weight(20)
            .yield(80, 120)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "enceladus"))
            .register();

    public static BedrockFluidDefinition UNKNOWWATER = BedrockFluidDefinition
            .builder(GTCEu.id("unknowwater"))
            .fluid(Objects.requireNonNull(GTMaterials.get("unknowwater"))::getFluid)
            .weight(20)
            .yield(40, 60)
            .depletionAmount(1)
            .depletionChance(100)
            .depletedYield(40)
            .dimensions(getDim("kubejs", "barnarda"))
            .register();

    public static void init() {
        toReRegister.forEach(GTRegistries.BEDROCK_FLUID_DEFINITIONS::registerOrOverride);
    }

    public static Set<ResourceKey<Level>> getDim(String namespace, String path) {
        return Set.of(ResourceKey.create(Registries.DIMENSION,
                new ResourceLocation(namespace, path)));
    }

    public static Set<ResourceKey<Level>> nether() {
        return Set.of(Level.NETHER);
    }

    public static Set<ResourceKey<Level>> overworld() {
        return Set.of(Level.OVERWORLD);
    }
}
