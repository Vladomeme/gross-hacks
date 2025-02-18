package net.grosshacks.main.util;

import net.grosshacks.main.wallet.WalletListWidget;
import net.minecraft.item.ItemStack;

public interface MixinUtil {

    ItemStack gh$getTrident();
    float gh$getTridentScale();
    ItemStack gh$getLastTrident();
    void gh$updateEntries();
    WalletListWidget gh$getWalletWidget();
}
