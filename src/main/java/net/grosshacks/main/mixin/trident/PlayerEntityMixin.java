package net.grosshacks.main.mixin.trident;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.MixinUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements MixinUtil {

    @Unique ItemStack lastTrident;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void gh$tick(CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.tridentCIT && getMainHandStack().getItem().equals(Items.TRIDENT))
            lastTrident = getMainHandStack();
    }

    @Unique
    public ItemStack gh$getLastTrident() {
        return lastTrident;
    }

    @SuppressWarnings("unused")
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
