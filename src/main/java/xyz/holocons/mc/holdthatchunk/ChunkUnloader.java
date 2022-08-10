package xyz.holocons.mc.holdthatchunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkUnloader {

    private class DelayedChunkUnload {

        private final ClientboundForgetLevelChunkPacket packet;
        private final int expiration;
        private boolean canceled;
        private DelayedChunkUnload next;

        public DelayedChunkUnload(ClientboundForgetLevelChunkPacket packet, int expiration) {
            this.packet = packet;
            this.expiration = expiration;
            this.canceled = false;
            this.next = null;
        }
    }

    private final Long2ObjectOpenHashMap<DelayedChunkUnload> chunkUnloadMap;
    private DelayedChunkUnload first;
    private DelayedChunkUnload last;
    private int tick;
    private ClientPacketListener listener;
    private Minecraft minecraft;

    public ChunkUnloader() {
        this.chunkUnloadMap = new Long2ObjectOpenHashMap<>();
        ClientChunkEvents.CHUNK_LOAD.register(this::onChunkLoad);
        ClientTickEvents.END_WORLD_TICK.register(this::onEndTick);
        ClientPlayConnectionEvents.INIT.register(this::onPlayInit);
    }

    private static long chunkKey(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public void onChunkUnload(ClientboundForgetLevelChunkPacket packet) {
        // Called from Netty thread
        minecraft.execute(() -> {
            final var newChunkUnload = new DelayedChunkUnload(packet, tick + HoldThatChunkMod.CONFIG.delay);
            final var oldChunkUnload = chunkUnloadMap.put(chunkKey(packet.getX(), packet.getZ()), newChunkUnload);
            if (oldChunkUnload != null) {
                oldChunkUnload.canceled = true;
            }
            if (first == null) {
                first = newChunkUnload;
                last = first;
            } else {
                last.next = newChunkUnload;
                last = last.next;
            }
        });
    }

    private void onChunkLoad(ClientLevel world, LevelChunk chunk) {
        final var oldChunkUnload = chunkUnloadMap.remove(chunkKey(chunk.getPos().x, chunk.getPos().z));
        if (oldChunkUnload != null) {
            oldChunkUnload.canceled = true;
        }
    }

    private void onEndTick(ClientLevel world) {
        tick++;
        if (first == null || tick < first.expiration) {
            return;
        }
        if (!first.canceled) {
            listener.handleForgetLevelChunk(first.packet);
        }
        first = first.next;
    }

    private void onPlayInit(ClientPacketListener handler, Minecraft client) {
        // Called from Netty thread
        client.executeBlocking(() -> {
            chunkUnloadMap.clear();
            first = null;
            tick = -1;
            listener = handler;
            minecraft = client;
        });
    }
}
