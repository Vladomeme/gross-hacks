package net.grosshacks.main.mixin;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//config -> disableBlockInteractions
@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {

    @Invoker("getPendingUpdateManager")
    PendingUpdateManager gh$getPendingUpdateManager();
}
