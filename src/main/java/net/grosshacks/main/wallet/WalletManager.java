package net.grosshacks.main.wallet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WalletManager {

    final static Set<String> WALLETS = new HashSet<>(List.of("Experience Flask", "Experience Bucket", "Crystal Cluster",
            "Crystal Collector", "Piggy Bank", "Bag of Hoarding", "Sketched Bag of Hoarding"));

    static final List<Withdrawal> entries = WithdrawalIO.read();
    static boolean walletAvailable = false;

    static boolean clickReady = true;
    static int clickCD = 5;

    public static void tick() {
        if (clickCD > 0) {
            clickCD--;
            if (clickCD == 0) clickReady = true;
        }
    }

    public static void onClick() {
        clickReady = false;
        clickCD = 5;
    }

    public static void checkWallet() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            walletAvailable = false;
            return;
        }

        for (ItemStack stack : player.getInventory().main) {
            if (stack.getNbt() == null) continue;
            String name = stack.getNbt().getCompound("plain").getCompound("display").getString("Name");
            if (WALLETS.contains(name)) {
                walletAvailable = true;
                return;
            }
        }
        walletAvailable = false;
    }

    public static boolean isWalletAvailable() {
        return walletAvailable;
    }

    public static ItemStack getRPItem(ItemStack stack, String name, String UUID, int count) {
        NbtCompound nbt = new NbtCompound();
        NbtCompound plain = new NbtCompound();
        nbt.put("plain", plain);
        NbtCompound display = new NbtCompound();
        plain.put("display", display);
        display.putString("Name", name);

        if (!UUID.isEmpty()) {
            NbtCompound monumenta = new NbtCompound();
            nbt.put("Monumenta", monumenta);
            NbtCompound playerModified = new NbtCompound();
            monumenta.put("PlayerModified", playerModified);
            NbtCompound infusions = new NbtCompound();
            playerModified.put("Infusions", infusions);
            NbtCompound hope = new NbtCompound();
            infusions.put("Hope", hope);
            hope.putString("Infuser", UUID);
        }

        stack.setNbt(nbt);
        stack.setCount(count);
        return stack;
    }

    public static void removeEntry(Withdrawal withdrawal) {
        entries.remove(withdrawal);
    }

    public static void moveUp(Withdrawal withdrawal) {
        if (!clickReady) return;
        onClick();

        int index = entries.indexOf(withdrawal);
        if (index == -1) throw new RuntimeException("Moved withdrawal entry could not be found in the wallet manager");
        if (index == 0) throw new RuntimeException("Tried to move up a top-most entry");

        Collections.swap(entries, index, index - 1);
        WithdrawalIO.shouldSave = true;
    }

    public static void moveDown(Withdrawal withdrawal) {
        if (!clickReady) return;
        onClick();

        int index = entries.indexOf(withdrawal);
        if (index == -1) throw new RuntimeException("Moved withdrawal entry could not be found in the wallet manager");
        if (index == entries.size() - 1) throw new RuntimeException("Tried to move down a bottom-most entry");

        Collections.swap(entries, index, index + 1);
        WithdrawalIO.shouldSave = true;

    }
}
