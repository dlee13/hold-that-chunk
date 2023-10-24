package xyz.holocons.mc.holdthatchunk.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import xyz.holocons.mc.holdthatchunk.HoldThatChunkMod;

@Mixin(ClientboundForgetLevelChunkPacket.class)
abstract class ClientboundForgetLevelChunkPacketMixin {

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;handleForgetLevelChunk(Lnet/minecraft/network/protocol/game/ClientboundForgetLevelChunkPacket;)V"))
    private void deferChunkUnload(ClientGamePacketListener listener, ClientboundForgetLevelChunkPacket packet) {
        HoldThatChunkMod.UNLOADER.onChunkUnload(packet);
    }
}
