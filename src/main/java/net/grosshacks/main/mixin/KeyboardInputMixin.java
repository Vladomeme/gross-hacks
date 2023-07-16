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

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tick(boolean slowDown, CallbackInfo ci) {
        if(GrossHacksConfig.INSTANCE.disable_unmouting) {
            if (MinecraftClient.getInstance().player.hasVehicle()) this.sneaking = GrossHacks.unmountKey.wasPressed();
            GrossHacks.unmountKey.reset();
        }
    }
}
