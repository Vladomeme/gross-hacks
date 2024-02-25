package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.ItemDataAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ItemDataAccessor {

    @Unique
    final NbtCompound latestTridentData = new NbtCompound();

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.thrown_trident_texture
                && MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.getInventory().getMainHandStack().getItem().toString().equals("trident")) {
            latestTridentData.put("LatestTridentData", MinecraftClient.getInstance().player.getInventory().getMainHandStack().getNbt());
        }
    }

    @Override
    public NbtCompound getLatestTridentData(){
        return latestTridentData;
    }

}
