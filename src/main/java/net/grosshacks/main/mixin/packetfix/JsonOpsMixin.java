package net.grosshacks.main.mixin.packetfix;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.grosshacks.main.GrossHacksConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JsonOps.class)
public class JsonOpsMixin {

	@Inject(method = "getBooleanValue(Lcom/google/gson/JsonElement;)Lcom/mojang/serialization/DataResult;", remap = false, cancellable = true,
			at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;error(Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;"))
	private void forceBooleanInput(JsonElement input, CallbackInfoReturnable<DataResult<Boolean>> cir) {
		if (GrossHacksConfig.INSTANCE.chatPacketFix) cir.setReturnValue(DataResult.success(false));
	}
}
