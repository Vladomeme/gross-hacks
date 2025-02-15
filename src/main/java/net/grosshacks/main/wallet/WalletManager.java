package net.grosshacks.main.wallet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class WalletManager {

    final static List<String> WALLETS = List.of("Experience Flask", "Experience Bucket", "Crystal Cluster", "Crystal Collector", "Piggy Bank", "Bag of Hoarding");

    static final List<Withdrawal> entries = WithdrawalIO.read();
    static boolean walletAvailable = false;

    static boolean withdrawReady = true;
    static int withdrawCD = 5;

    public static void tick() {
        if (withdrawCD > 0) {
            withdrawCD--;
            if (withdrawCD == 0) withdrawReady = true;
        }
    }

    public static void onWithdraw() {
        withdrawReady = false;
        withdrawCD = 5;
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
}
