package net.grosshacks.main.util;

import net.minecraft.item.ItemStack;

public interface MixinUtil {

    ItemStack gh$getTrident();
    float gh$getTridentScale();
    ItemStack gh$getLastTrident();
    void gh$unblockChat();
    boolean gh$isBlocked();
}
