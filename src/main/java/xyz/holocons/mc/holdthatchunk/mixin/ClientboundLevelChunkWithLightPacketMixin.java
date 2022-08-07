package xyz.holocons.mc.holdthatchunk.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import xyz.holocons.mc.holdthatchunk.HoldThatChunkMod;

@Mixin(ClientboundLevelChunkWithLightPacket.class)
public class ClientboundLevelChunkWithLightPacketMixin {

   @Inject(method = "handle", at = @At("HEAD"))
   private void cancelChunkUnload(ClientGamePacketListener listener, CallbackInfo info) {
      HoldThatChunkMod.UNLOADER.onChunkLoad((ClientboundLevelChunkWithLightPacket) (Object) this);
   }
}
