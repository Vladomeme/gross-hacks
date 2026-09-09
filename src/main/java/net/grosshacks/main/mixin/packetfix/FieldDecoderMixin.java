package net.grosshacks.main.mixin.packetfix;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.FieldDecoder;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FieldDecoder.class)
public abstract class FieldDecoderMixin<A> extends MapDecoder.Implementation<A> {

	//bypass for missing id field
	@SuppressWarnings("unchecked")
    @Inject(method = "decode", remap = false, cancellable = true,
			at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;error(Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;"))
	private <T> void gh$decode(DynamicOps<T> ops, MapLike<T> input, CallbackInfoReturnable<DataResult<A>> cir) {
		if (GrossHacksConfig.INSTANCE.chatPacketFix && getName().equals("id"))
			cir.setReturnValue((DataResult<A>) DataResult.success(Identifier.of(UUID.randomUUID().toString())));
	}

	@Accessor("name")
    abstract String getName();
}
