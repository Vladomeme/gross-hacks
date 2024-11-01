package net.grosshacks.main.mixin.trident;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.MixinUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity implements MixinUtil {

    @Shadow @Final private static ItemStack DEFAULT_STACK;
    @Unique ItemStack trident;
    @Unique boolean checked = false;
    @Unique float tridentScale = GrossHacksConfig.INSTANCE.tridentScale;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (!GrossHacksConfig.INSTANCE.tridentCIT || checked) return;
        getTridentInfo();
        checked = true;
    }

    @Unique
    private void getTridentInfo() {
        ItemStack item;
        PlayerEntity nearestPlayer = Objects.requireNonNull(MinecraftClient.getInstance().player);

        if (getOwner() != null) item = getOwner().getHandItems().iterator().next();
        else {
            nearestPlayer = getWorld().getClosestPlayer((TridentEntity) (Object) this, 10);
            if (nearestPlayer != null) item = nearestPlayer.getInventory().getMainHandStack();
            else return;
        }

        if (item.getItem().equals(Items.TRIDENT)) trident = item.copy();
        else if (((MixinUtil) nearestPlayer).gh$getLastTrident() != null)
            trident = ((MixinUtil) nearestPlayer).gh$getLastTrident().copy();
        else return;

        if (GrossHacksConfig.INSTANCE.customProjectiles) checkCustomProjectile();
        if (GrossHacksConfig.INSTANCE.customScaling) checkCustomScale();
    }

    @Override
    public ItemStack gh$getTrident() {
        return trident;
    }

    @Override
    public float gh$getTridentScale() {
        return tridentScale;
    }

    @Unique
    public void checkCustomScale() {
        Float scale = GrossHacks.tridentScales.get(trident.getName().getString());
        if (scale != null) tridentScale = scale;
    }

    @Unique
    public void checkCustomProjectile() {
        NbtCompound nbt = trident.getNbt();
        if (nbt == null) return;

        //Replacing monumenta 'plain' name
        if (nbt.contains("plain")) {
            String name = nbt.getCompound("plain").getCompound("display").getString("Name");

            if (GrossHacks.projectileList.contains(name.toLowerCase()
                    .replace("(", "").replace(")", "").replace("-", "").replace("'", ""))) {
                nbt.getCompound("plain").getCompound("display").putString("Name", name + "_projectile");
            }
        }

        //Replacing vanilla name
        if (nbt.contains("display")) {
            String name = nbt.getCompound("display").getString("Name");

            if (GrossHacks.projectileList.contains(name.toLowerCase()
                    .replace("(", "").replace(")", "").replace("-", "").replace("'", ""))) {
                nbt.getCompound("display").putString("Name", name + "_projectile");
            }
        }
        trident.setNbt(nbt);
    }

    @SuppressWarnings("unused")
    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world, DEFAULT_STACK);
    }
}
