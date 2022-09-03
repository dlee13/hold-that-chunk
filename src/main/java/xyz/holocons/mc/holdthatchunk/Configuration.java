package xyz.holocons.mc.holdthatchunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;

public class Configuration {

    private static final Path configPath = FabricLoader.getInstance().getConfigDir().normalize()
            .resolve(HoldThatChunkMod.MOD_ID + ".json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public int chunkUnloadDelay = 20 * 60 * 60;
    public boolean ignoreServerRenderDistance = false;

    public void deserialize(JsonObject jsonObject) {
        if (jsonObject == null) {
            HoldThatChunkMod.LOGGER.warn("[{}] Config was empty", HoldThatChunkMod.MOD_ID);
        } else {
            try {
                chunkUnloadDelay = Math.min(jsonObject.get("chunkUnloadDelayInTicks").getAsInt(), 20 * 60 * 60 * 24);
            } catch (Exception e) {
                HoldThatChunkMod.LOGGER.warn("[{}] Could not parse chunkUnloadDelayInTicks option",
                        HoldThatChunkMod.MOD_ID);
            }
            try {
                ignoreServerRenderDistance = jsonObject.get("ignoreServerRenderDistance").getAsBoolean();
            } catch (Exception e) {
                HoldThatChunkMod.LOGGER.warn("[{}] Could not parse ignoreServerRenderDistance option",
                        HoldThatChunkMod.MOD_ID);
            }
        }
    }

    public JsonObject serialize() {
        final var jsonObject = new JsonObject();
        jsonObject.addProperty("chunkUnloadDelayInTicks", chunkUnloadDelay);
        jsonObject.addProperty("ignoreServerRenderDistance", ignoreServerRenderDistance);
        return jsonObject;
    }

    public void load() {
        final var configFile = configPath.toFile();
        if (!configFile.exists()) {
            save();
        } else {
            try (final var reader = Files.newBufferedReader(configPath)) {
                deserialize(gson.fromJson(reader, JsonObject.class));
            } catch (IOException e) {
                HoldThatChunkMod.LOGGER.error("[{}] Could not read config", HoldThatChunkMod.MOD_ID, e);
            }
        }
    }

    public void save() {
        try (final var writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(serialize(), JsonObject.class, writer);
        } catch (IOException e) {
            HoldThatChunkMod.LOGGER.error("[{}] Could not write config", HoldThatChunkMod.MOD_ID, e);
        }
    }
}
