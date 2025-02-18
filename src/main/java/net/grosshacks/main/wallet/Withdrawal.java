package net.grosshacks.main.wallet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public final class Withdrawal {

    private final ItemStack left;
    private final ItemStack right;

    public Withdrawal(ItemStack left, ItemStack right) {
        this.left = left;
        this.right = right;
    }

    public Withdrawal(String itemType, String itemName, int count) {
        ItemStack stack = Registries.ITEM.get(Identifier.of("minecraft", itemType)).getDefaultStack();
        this.left = WalletManager.getRPItem(stack, itemName, "", count);
        this.right = null;
    }

    public Withdrawal(String itemTypeLeft, String itemNameLeft, int countLeft, String itemTypeRight, String itemNameRight, int countRight) {
        ItemStack left = Registries.ITEM.get(Identifier.of("minecraft", itemTypeLeft)).getDefaultStack();
        this.left = WalletManager.getRPItem(left, itemNameLeft, "", countLeft);

        ItemStack right = Registries.ITEM.get(Identifier.of("minecraft", itemTypeRight)).getDefaultStack();
        this.right = WalletManager.getRPItem(right, itemNameRight, "", countRight);
    }

    public static Withdrawal fromProto(WithdrawalIO.ProtoWithdrawal proto) {
        if (proto.itemTypeRight() != null && !proto.itemTypeRight().isEmpty()) {
            return new Withdrawal(proto.itemTypeLeft(), proto.itemNameLeft(), proto.countLeft(),
                    proto.itemTypeRight(), proto.itemNameRight(), proto.countRight());
        }
        else return new Withdrawal(proto.itemTypeLeft(), proto.itemNameLeft(), proto.countLeft());
    }

    public void withdraw() {
        if (!WalletManager.clickReady) return;
        WalletManager.onClick();

        ClientPlayNetworkHandler nh = MinecraftClient.getInstance().getNetworkHandler();
        if (nh == null) return;

        nh.sendCommand("wallet withdraw " + left.getCount() + " " + getName(left));
        if (right != null) nh.sendCommand("wallet withdraw " + right.getCount() + " " + getName(right));
    }

    public void remove() {
        WalletManager.removeEntry(this);
        WithdrawalIO.shouldSave = true;
    }

    public static String getName(ItemStack stack) {
        return Objects.requireNonNull(stack.getNbt()).getCompound("plain").getCompound("display").getString("Name");
    }

    public ItemStack left() {
        return left;
    }

    public ItemStack right() {
        return right;
    }
}
