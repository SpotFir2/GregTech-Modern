package com.gregtechceu.gtceu.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.DimensionMarker;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.integration.kjs.GTRegistryInfo;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModLoader;

import com.tterrag.registrate.util.entry.BlockEntry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

@SuppressWarnings("unused")
public class GTDimensionMarkers {

    static {
        GTRegistries.DIMENSION_MARKERS.unfreeze();
        REGISTRATE.creativeModeTab(() -> null);
    }

    public static final BlockEntry<Block> OVERWORLD_MARKER = createMarker("overworld");
    public static final BlockEntry<Block> NETHER_MARKER = createMarker("the_nether");
    public static final BlockEntry<Block> END_MARKER = createMarker("the_end");

    public static final DimensionMarker OVERWORLD = createAndRegister(Level.OVERWORLD.location(), 0,
            () -> OVERWORLD_MARKER, "block.gtceu.overworld_marker");
    public static final DimensionMarker NETHER = createAndRegister(Level.NETHER.location(), 3,
            () -> NETHER_MARKER, "block.gtceu.the_nether_marker");
    public static final DimensionMarker END = createAndRegister(Level.END.location(), 6,
            () -> END_MARKER, "block.gtceu.the_end_marker");
    public static final DimensionMarker MOON = createAndRegister(new ResourceLocation("ad_astra:moon"),
            1, new ResourceLocation("ad_astra:moon_stone"), "dimension.ad_astra:moon");
    public static final DimensionMarker MARS = createAndRegister(new ResourceLocation("ad_astra:mars"),
            2, new ResourceLocation("ad_astra:mars_stone"), "dimension.ad_astra:mars");
    public static final DimensionMarker VENUS = createAndRegister(new ResourceLocation("ad_astra:venus"),
            3, new ResourceLocation("ad_astra:venus_stone"), "dimension.ad_astra:venus");
    public static final DimensionMarker MERCURY = createAndRegister(new ResourceLocation("ad_astra:mercury"),
            3, new ResourceLocation("ad_astra:mercury_stone"), "dimension.ad_astra:mercury");
    public static final DimensionMarker GLACIO = createAndRegister(new ResourceLocation("ad_astra:glacio"),
            7, new ResourceLocation("ad_astra:glacio_stone"), "dimension.ad_astra:glacio");
    public static final DimensionMarker ANCIENT_WORLD = createAndRegister(new ResourceLocation("kubejs:ancient_world"),
            0, new ResourceLocation("kubejs:reactor_core"), "dimension.kubejs:ancient_world");
    public static final DimensionMarker titan = createAndRegister(new ResourceLocation("kubejs:titan"),
            6, new ResourceLocation("kubejs:titanstone"), "dimension.kubejs:titan");
    public static final DimensionMarker pluto = createAndRegister(new ResourceLocation("kubejs:pluto"),
            6, new ResourceLocation("kubejs:plutostone"), "dimension.kubejs:pluto");
    public static final DimensionMarker io = createAndRegister(new ResourceLocation("kubejs:io"),
            5, new ResourceLocation("kubejs:iostone"), "dimension.kubejs:io");
    public static final DimensionMarker ganymede = createAndRegister(new ResourceLocation("kubejs:ganymede"),
            5, new ResourceLocation("kubejs:ganymedestone"), "dimension.kubejs:ganymede");
    public static final DimensionMarker enceladus = createAndRegister(new ResourceLocation("kubejs:enceladus"),
            6, new ResourceLocation("kubejs:enceladusstone"), "dimension.kubejs:enceladus");
    public static final DimensionMarker ceres = createAndRegister(new ResourceLocation("kubejs:ceres"),
            4, new ResourceLocation("kubejs:ceresstone"), "dimension.kubejs:ceres");
    public static final DimensionMarker barnarda = createAndRegister(new ResourceLocation("kubejs:barnarda"),
            8, new ResourceLocation("kubejs:barnarda_wood"), "dimension.kubejs:barnarda");

    public static DimensionMarker createAndRegister(ResourceLocation dim, int tier, ResourceLocation itemKey,
                                                    @Nullable String overrideName) {
        DimensionMarker marker = new DimensionMarker(tier, itemKey, overrideName);
        marker.register(dim);
        return marker;
    }

    public static DimensionMarker createAndRegister(ResourceLocation dim, int tier, Supplier<ItemLike> supplier,
                                                    @Nullable String overrideName) {
        DimensionMarker marker = new DimensionMarker(tier, supplier, overrideName);
        marker.register(dim);
        return marker;
    }

    private static BlockEntry<Block> createMarker(String name) {
        return REGISTRATE.block("%s_marker".formatted(name), Block::new)
                .lang(FormattingUtil.toEnglishName(name))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cube(ctx.getName(),
                        prov.modLoc("block/dim_markers/%s/down".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/up".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/north".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/south".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/east".formatted(name)),
                        prov.modLoc("block/dim_markers/%s/west".formatted(name)))
                        .texture("particle", "#north")
                        .guiLight(BlockModel.GuiLight.FRONT)))
                .simpleItem()
                .register();
    }

    public static void init() {
        ModLoader.get().postEvent(new GTCEuAPI.RegisterEvent<>(GTRegistries.DIMENSION_MARKERS, DimensionMarker.class));
        if (GTCEu.isKubeJSLoaded()) {
            GTRegistryInfo.registerFor(GTRegistries.DIMENSION_MARKERS.getRegistryName());
        }
        GTRegistries.DIMENSION_MARKERS.freeze();
    }
}
