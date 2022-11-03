package xyz.holocons.mc.holdthatchunk;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ModMenuIntegration implements ModMenuApi {

    private static final Component titleComponent = Component.literal("Hold That Chunk");
    private static final Component chunkUnloadDistanceComponent = Component.literal("Chunk Unload Distance");
    private static final Component ignoreServerRenderDistanceComponent = Component
            .literal("Ignore Server Render Distance");

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return new ClothConfigScreenFactory();
        } else if (FabricLoader.getInstance().isModLoaded("yet-another-config-lib")) {
            return new YetAnotherConfigLibScreenFactory();
        } else {
            return parent -> null;
        }
    }

    private class ClothConfigScreenFactory implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            final var defaultConfig = new Configuration();
            final var builder = ConfigBuilder.create()
                    .setTitle(titleComponent)
                    .setSavingRunnable(HoldThatChunkMod.CONFIG::save)
                    .setParentScreen(parent);
            builder.getOrCreateCategory(titleComponent)
                    .addEntry(builder.entryBuilder()
                            .startIntSlider(
                                    chunkUnloadDistanceComponent,
                                    HoldThatChunkMod.CONFIG.chunkUnloadDistance,
                                    8,
                                    128)
                            .setDefaultValue(defaultConfig.chunkUnloadDistance)
                            .setSaveConsumer(value -> HoldThatChunkMod.CONFIG.chunkUnloadDistance = value)
                            .setTextGetter(i -> Component.literal(String.format("%d chunks", i)))
                            .build())
                    .addEntry(builder.entryBuilder()
                            .startBooleanToggle(
                                    ignoreServerRenderDistanceComponent,
                                    HoldThatChunkMod.CONFIG.ignoreServerRenderDistance)
                            .setDefaultValue(defaultConfig.ignoreServerRenderDistance)
                            .setSaveConsumer(Minecraft.getInstance().getCurrentServer() == null
                                    ? value -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance = value
                                    : null)
                            .build());
            return builder.build();
        }
    }

    private class YetAnotherConfigLibScreenFactory implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            final var defaultConfig = new Configuration();
            return YetAnotherConfigLib.createBuilder()
                    .title(titleComponent)
                    .save(HoldThatChunkMod.CONFIG::save)
                    .category(ConfigCategory.createBuilder()
                            .name(titleComponent)
                            .option(Option.createBuilder(int.class)
                                    .name(chunkUnloadDistanceComponent)
                                    .binding(
                                            defaultConfig.chunkUnloadDistance,
                                            () -> HoldThatChunkMod.CONFIG.chunkUnloadDistance,
                                            value -> HoldThatChunkMod.CONFIG.chunkUnloadDistance = value)
                                    .controller(opt -> new IntegerSliderController(
                                            opt,
                                            8,
                                            128,
                                            1,
                                            i -> Component.literal(String.format("%d chunks", i))))
                                    .build())
                            .option(Option.createBuilder(boolean.class)
                                    .name(ignoreServerRenderDistanceComponent)
                                    .binding(
                                            defaultConfig.ignoreServerRenderDistance,
                                            () -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance,
                                            value -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance = value)
                                    .controller(TickBoxController::new)
                                    .available(Minecraft.getInstance().getCurrentServer() == null)
                                    .build())
                            .build())
                    .build()
                    .generateScreen(parent);
        }
    }
}
