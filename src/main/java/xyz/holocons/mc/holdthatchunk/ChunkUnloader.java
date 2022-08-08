package xyz.holocons.mc.holdthatchunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;

public class ChunkUnloader {

    private class DelayedChunkUnload {

        private final ClientboundForgetLevelChunkPacket packet;
        private final long expiration;
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

    public ChunkUnloader() {
        this.chunkUnloadMap = new Long2ObjectOpenHashMap<>();
        ClientTickEvents.END_WORLD_TICK.register(this::onEndTick);
    }

    public static long chunkKey(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public void onChunkLoad(ClientboundLevelChunkWithLightPacket packet) {
        final var oldChunkUnload = chunkUnloadMap.remove(chunkKey(packet.getX(), packet.getZ()));
        if (oldChunkUnload != null) {
            oldChunkUnload.canceled = true;
        }
    }

    public void onChunkUnload(ClientboundForgetLevelChunkPacket packet) {
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

    public void onJoinWorld(ClientPacketListener listener) {
        chunkUnloadMap.clear();
        first = null;
        tick = -1;
        this.listener = listener;
    }
}
