package xyz.holocons.mc.holdthatchunk;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
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
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return new ClothConfigScreenFactory();
        } else if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
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
            final var chunkUnloadDistanceSlider = builder.entryBuilder()
                    .startIntSlider(
                            chunkUnloadDistanceComponent,
                            HoldThatChunkMod.CONFIG.chunkUnloadDistance,
                            8,
                            128)
                    .setDefaultValue(defaultConfig.chunkUnloadDistance)
                    .setSaveConsumer(value -> HoldThatChunkMod.CONFIG.chunkUnloadDistance = value)
                    .setTextGetter(i -> Component.literal(String.format("%d chunks", i)))
                    .build();
            final var ignoreServerRenderDistanceToggle = builder.entryBuilder()
                    .startBooleanToggle(
                            ignoreServerRenderDistanceComponent,
                            HoldThatChunkMod.CONFIG.ignoreServerRenderDistance)
                    .setDefaultValue(defaultConfig.ignoreServerRenderDistance)
                    .setSaveConsumer(value -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance = value)
                    .build();
            ignoreServerRenderDistanceToggle.setEditable(Minecraft.getInstance().getCurrentServer() == null);
            builder.getOrCreateCategory(titleComponent)
                    .addEntry(chunkUnloadDistanceSlider)
                    .addEntry(ignoreServerRenderDistanceToggle);
            return builder.build();
        }
    }

    private class YetAnotherConfigLibScreenFactory implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            final var defaultConfig = new Configuration();
            final var chunkUnloadDistanceSlider = Option.<Integer>createBuilder()
                    .name(chunkUnloadDistanceComponent)
                    .binding(
                            defaultConfig.chunkUnloadDistance,
                            () -> HoldThatChunkMod.CONFIG.chunkUnloadDistance,
                            value -> HoldThatChunkMod.CONFIG.chunkUnloadDistance = value)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                            .range(8, 128)
                            .step(1)
                            .formatValue(i -> Component.literal(String.format("%d chunks", i))))
                    .build();
            final var ignoreServerRenderDistanceToggle = Option.<Boolean>createBuilder()
                    .name(ignoreServerRenderDistanceComponent)
                    .binding(
                            defaultConfig.ignoreServerRenderDistance,
                            () -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance,
                            value -> HoldThatChunkMod.CONFIG.ignoreServerRenderDistance = value)
                    .controller(TickBoxControllerBuilder::create)
                    .available(Minecraft.getInstance().getCurrentServer() == null)
                    .build();
            return YetAnotherConfigLib.createBuilder()
                    .title(titleComponent)
                    .save(HoldThatChunkMod.CONFIG::save)
                    .category(ConfigCategory.createBuilder()
                            .name(titleComponent)
                            .option(chunkUnloadDistanceSlider)
                            .option(ignoreServerRenderDistanceToggle)
                            .build())
                    .build()
                    .generateScreen(parent);
        }
    }
}
