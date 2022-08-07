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
         .resolve("holdthatchunk.json");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

   public int delay = 12000;

   public void deserialize(JsonObject data) {
      if (data == null) {
         HoldThatChunkMod.LOGGER.error("Config was empty");
      } else {
         try {
            delay = data.get("chunkUnloadDelayInTicks").getAsInt();
         } catch (Exception e) {
            HoldThatChunkMod.LOGGER.error("Could not parse config", e);
         }
      }
   }

   public JsonObject serialize() {
      JsonObject root = new JsonObject();
      root.addProperty("chunkUnloadDelayInTicks", delay);
      return root;
   }

   public void load() {
      final var configFile = configPath.toFile();
      if (!configFile.exists()) {
         save();
      } else {
         try (final var reader = Files.newBufferedReader(configPath)) {
            deserialize(gson.fromJson(reader, JsonObject.class));
         } catch (IOException e) {
            HoldThatChunkMod.LOGGER.error("Could not read config", e);
         }
      }
   }

   public void save() {
      try (final var writer = Files.newBufferedWriter(configPath)) {
         gson.toJson(serialize(), JsonObject.class, writer);
      } catch (IOException e) {
         HoldThatChunkMod.LOGGER.error("Could not write config", e);
      }
   }
}
