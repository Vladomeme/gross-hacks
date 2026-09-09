package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//config -> rebindDismounting
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void gh$tick(boolean slowDown, float f, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.hasVehicle()) {
            if (GrossHacksConfig.INSTANCE.rebindDismounting) this.sneaking = GrossHacks.dismountKey.wasPressed();
        }
        ((KeyBindingAccessor) GrossHacks.dismountKey).gh$reset();
    }
}
