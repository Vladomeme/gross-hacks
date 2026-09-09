package net.grosshacks.main.feature.wallet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
        ItemStack stack = Registries.ITEM.get(Identifier.of(itemType)).getDefaultStack();
        this.left = WalletManager.getRPItem(stack, itemName, null, count);
        this.right = null;
    }

    public Withdrawal(String itemTypeLeft, String itemNameLeft, int countLeft, String itemTypeRight, String itemNameRight, int countRight) {
        ItemStack left = Registries.ITEM.get(Identifier.of(itemTypeLeft)).getDefaultStack();
        this.left = WalletManager.getRPItem(left, itemNameLeft, null, countLeft);

        ItemStack right = Registries.ITEM.get(Identifier.of(itemTypeRight)).getDefaultStack();
        this.right = WalletManager.getRPItem(right, itemNameRight, null, countRight);
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

    @SuppressWarnings({"deprecation", "DataFlowIssue"})
    public static String getName(ItemStack stack) {
        ComponentMap components = stack.getComponents();
        if (!components.contains(DataComponentTypes.CUSTOM_DATA)) return "";

        NbtCompound nbt = components.get(DataComponentTypes.CUSTOM_DATA).getNbt();
        return Objects.requireNonNull(nbt).getCompound("plain").getCompound("display").getString("Name");
    }

    public ItemStack left() {
        return left;
    }

    public ItemStack right() {
        return right;
    }
}
