package net.grosshacks.main.util;

import net.minecraft.nbt.NbtCompound;

public interface ItemDataAccessor {
    NbtCompound getTridentItemData();
    Float getTridentScale();
    NbtCompound getLatestTridentData();
}
