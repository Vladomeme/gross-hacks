package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.ItemDataAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity implements ItemDataAccessor {

    NbtCompound tridentItemData = new NbtCompound();
    Float tridentScale;
    NbtCompound ownerNbt = new NbtCompound();
    PlayerEntity nearestPlayer;
    PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.thrown_trident_texture && !tridentItemData.contains("TridentItemData")) {
            /*
            Since monumenta hacks the tridents to be infinitely throwable, they lose all data that links them to the owner.
            Getting the nearest player is a pretty reliable method, but perhaps not the most efficient.
             */
            if (this.getOwner() == null) {
                nearestPlayer = this.world.getClosestPlayer(this, 10);
                if(nearestPlayer != null) {
                    inventory = nearestPlayer.getInventory();
                }
            }
            /*
            Tridents thrown by mobs keep the owner data, so we can simply use that.
             */
            else {
                this.getOwner().writeNbt(ownerNbt);
            }
            if (nearestPlayer != null || ownerNbt != null) {
                if (ownerNbt.getList("HandItems", 10).getCompound(0).getString("id").equals("minecraft:trident") ||
                        inventory.getMainHandStack().getItem().toString().equals("trident")) {
                    if (this.getOwner() instanceof PlayerEntity || nearestPlayer != null) {
                        tridentItemData.put("TridentItemData", setTag(inventory.getMainHandStack().getNbt().copy()));
                    } else {
                        tridentItemData.put("TridentItemData", ownerNbt.getList("HandItems", 10).getCompound(0));
                    }
                }
                /*
                A way to get the trident data in situations when player switches to a different item right after throwing the trident.
                 */
                else {
                    tridentItemData.put("TridentItemData", setTag(((ItemDataAccessor) MinecraftClient.getInstance().player).getLatestTridentData().getCompound("LatestTridentData")));
                }
            }
            if (GrossHacksConfig.INSTANCE.per_trident_scaling) {
                setTridentScale();
            }
        }
    }

    @Override
    public NbtCompound getTridentItemData() {
        if (!tridentItemData.getCompound("TridentItemData").contains("id")){
            tridentItemData.getCompound("TridentItemData").putString("id", "minecraft:trident");
            tridentItemData.getCompound("TridentItemData").putByte("Count", (byte) 1);
        }
        if (GrossHacksConfig.INSTANCE.custom_trident_projectile) {
            tridentItemData = checkProjectile(tridentItemData);
        }
        return tridentItemData;
    }

    @Override
    public Float getTridentScale() {
        return tridentScale;
    }

    public void setTridentScale() {
        GrossHacks.tridentScales.forEach((name, scale)-> {
            if (name.equals(tridentItemData.getCompound("TridentItemData").getCompound("tag").getCompound("plain").getCompound("display").getString("Name"))) {
                tridentScale = scale;
            }
        });
    }

    public NbtCompound setTag(NbtCompound tridentPlainData) {
        NbtCompound tagCompound = new NbtCompound();
        tagCompound.put("tag", tridentPlainData);
        return tagCompound;
    }

    /*
    Checks if trident has a projectile, and edits the name of the rendered item.
    */
    public NbtCompound checkProjectile(NbtCompound tridentData) {
        String name = tridentData.getCompound("TridentItemData").getCompound("tag").getCompound("plain").getCompound("display").getString("Name");
        GrossHacks.projectileList.forEach(proj -> {
            if (proj.equals(name.toLowerCase()
                    .replace("(","")
                    .replace(")","")
                    .replace("-","")
                    .replace("'",""))) {
                tridentData.getCompound("TridentItemData").getCompound("tag").getCompound("plain").getCompound("display").remove("Name");
                tridentData.getCompound("TridentItemData").getCompound("tag").getCompound("plain").getCompound("display").putString("Name", name + "_projectile");
            }
        });
        return tridentData;
    }
}
