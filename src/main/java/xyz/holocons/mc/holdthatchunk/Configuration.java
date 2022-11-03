package xyz.holocons.mc.holdthatchunk;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;

public class Configuration {

    private static final Path configPath = FabricLoader.getInstance().getConfigDir().normalize()
            .resolve(HoldThatChunkMod.MOD_ID + ".json");
    private static final Gson gson = new Gson();

    public int chunkUnloadDistance = 64;
    public boolean ignoreServerRenderDistance = false;

    public void deserialize(JsonObject jsonObject) {
        if (jsonObject == null) {
            HoldThatChunkMod.LOGGER.warn("[{}] Config was empty", HoldThatChunkMod.MOD_ID);
        } else {
            try {
                chunkUnloadDistance = jsonObject.get("chunkUnloadDistance").getAsInt();
            } catch (Exception e) {
                HoldThatChunkMod.LOGGER.warn("[{}] Could not parse chunkUnloadDistance option",
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
        jsonObject.addProperty("chunkUnloadDistance", chunkUnloadDistance);
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
            } catch (Exception e) {
                HoldThatChunkMod.LOGGER.error("[{}] Could not read config", HoldThatChunkMod.MOD_ID, e);
            }
        }
    }

    public void save() {
        try (final var writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(serialize(), JsonObject.class, writer);
        } catch (Exception e) {
            HoldThatChunkMod.LOGGER.error("[{}] Could not write config", HoldThatChunkMod.MOD_ID, e);
        }
    }
}
