package xyz.holocons.mc.holdthatchunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

public final class HoldThatChunkMod implements ClientModInitializer {

    public static final String MOD_ID = "holdthatchunk";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Configuration CONFIG = new Configuration();
    public static final ChunkUnloader UNLOADER = new ChunkUnloader();

    @Override
    public void onInitializeClient() {
        CONFIG.load();
    }
}
