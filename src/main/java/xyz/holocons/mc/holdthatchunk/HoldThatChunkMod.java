package xyz.holocons.mc.holdthatchunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

public final class HoldThatChunkMod implements ClientModInitializer {

   public static final Logger LOGGER = LogManager.getLogger();
   public static final Configuration CONFIG = new Configuration();
   public static final ChunkUnloader UNLOADER = new ChunkUnloader();

   @Override
   public void onInitializeClient() {
      CONFIG.load();
   }
}
