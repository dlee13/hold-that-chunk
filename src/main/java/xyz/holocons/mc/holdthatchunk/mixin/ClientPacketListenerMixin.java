package xyz.holocons.mc.holdthatchunk.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import xyz.holocons.mc.holdthatchunk.HoldThatChunkMod;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

    private ClientPacketListenerMixin() {
        super(null, null, null);
    }

    @Redirect(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;chunkRadius()I"))
    private int getRenderDistanceOnLoginPacket(ClientboundLoginPacket packet) {
        return HoldThatChunkMod.CONFIG.ignoreServerRenderDistance
                ? minecraft.options.renderDistance().get()
                : packet.chunkRadius();
    }

    @Redirect(method = "handleSetChunkCacheRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetChunkCacheRadiusPacket;getRadius()I"))
    private int getRenderDistanceOnChunkCacheRadiusPacket(ClientboundSetChunkCacheRadiusPacket packet) {
        return HoldThatChunkMod.CONFIG.ignoreServerRenderDistance
                ? minecraft.options.renderDistance().get()
                : packet.getRadius();
    }
}
