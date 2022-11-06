package xyz.holocons.mc.holdthatchunk;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkUnloader {

    private final ObjectOpenHashSet<ChunkPos> chunkUnloadSet;
    private ChunkPos lastUnloadPosition;
    private ClientPacketListener listener;
    private Minecraft minecraft;

    public ChunkUnloader() {
        this.chunkUnloadSet = new ObjectOpenHashSet<>();
        ClientChunkEvents.CHUNK_LOAD.register(this::onChunkLoad);
        ClientPlayConnectionEvents.INIT.register(this::onPlayInit);
    }

    public void onChunkUnload(ClientboundForgetLevelChunkPacket packet) {
        // Called from Netty thread
        minecraft.execute(() -> chunkUnloadSet.add(new ChunkPos(packet.getX(), packet.getZ())));
    }

    private void onChunkLoad(ClientLevel world, LevelChunk chunk) {
        chunkUnloadSet.remove(chunk.getPos());
        final var currentPosition = minecraft.player.chunkPosition();
        if (lastUnloadPosition.getChessboardDistance(currentPosition) < 8 || listener.getLevel() == null) {
            return;
        }
        lastUnloadPosition = currentPosition;
        final var iterator = chunkUnloadSet.iterator();
        while (iterator.hasNext()) {
            final var position = iterator.next();
            if (position.getChessboardDistance(currentPosition) > HoldThatChunkMod.CONFIG.chunkUnloadDistance) {
                listener.handleForgetLevelChunk(new ClientboundForgetLevelChunkPacket(position.x, position.z));
                iterator.remove();
            }
        }
    }

    private void onPlayInit(ClientPacketListener handler, Minecraft client) {
        // Called from Netty thread
        client.executeBlocking(() -> {
            chunkUnloadSet.clear();
            lastUnloadPosition = ChunkPos.ZERO;
            listener = handler;
            minecraft = client;
        });
    }
}
