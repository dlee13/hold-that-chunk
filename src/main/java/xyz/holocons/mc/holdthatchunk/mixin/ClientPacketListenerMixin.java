package xyz.holocons.mc.holdthatchunk.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import xyz.holocons.mc.holdthatchunk.HoldThatChunkMod;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void initializeUnloader(ClientboundLoginPacket packet, CallbackInfo info) {
        HoldThatChunkMod.UNLOADER.onJoinWorld((ClientPacketListener) (Object) this);
    }
}
