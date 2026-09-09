package net.grosshacks.main.mixin.packetfix;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.handler.PacketCodecDispatcher;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PacketCodecDispatcher.class)
public class PacketCodecDispatcherMixin<B extends ByteBuf, V, T> {

	@Shadow @Final private List<PacketCodecDispatcher.PacketType<B, V, T>> packetTypes;

	@Inject(method = "decode(Lio/netty/buffer/ByteBuf;)Ljava/lang/Object;",
			at = @At(value = "INVOKE", target = "Lio/netty/handler/codec/DecoderException;<init>(Ljava/lang/String;)V"), cancellable = true)
	private void gh$replaceCodec(B byteBuf, CallbackInfoReturnable<V> cir, @Local int i) {
		if (!GrossHacksConfig.INSTANCE.chatPacketFix) return;
		if (packetTypes.get(i).id.equals("clientbound/minecraft:system_chat")) {
			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("[Gross Hacks] Couldn't fix message decoding. Do not report this.")
					.setStyle(Style.EMPTY.withColor(Formatting.RED)));
			cir.setReturnValue(null);
		}
	}
}
