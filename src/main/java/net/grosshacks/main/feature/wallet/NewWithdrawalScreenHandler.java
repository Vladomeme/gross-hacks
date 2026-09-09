package net.grosshacks.main.feature.wallet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class NewWithdrawalScreenHandler extends ScreenHandler {

    public final Inventory inventory;

    public NewWithdrawalScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2));
    }

    public NewWithdrawalScreenHandler(int syncId, PlayerInventory playerInventory, Inventory slotInventory) {
        super(new ScreenHandlerType<>(NewWithdrawalScreenHandler::new, FeatureSet.empty()), syncId);
        checkSize(slotInventory, 2);
        inventory = slotInventory;
        inventory.onOpen(playerInventory.player);

        addSlot(new Slot(inventory, 0, 28, 43));
        addSlot(new Slot(inventory, 1, 128, 43));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, 9 + (row * 9) + column, (column * 18) + 8, 133 + (row * 18)));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, (column * 18) + 8, 203));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex <= 1) slots.get(slotIndex).setStack(ItemStack.EMPTY);
        else {
            ItemStack stack = slots.get(slotIndex).getStack().copyWithCount(1);
            if (stack != ItemStack.EMPTY) setSlot(button, stack);
        }
    }

    public void setSlot(int slot, ItemStack itemStack) {
        inventory.setStack(slot, itemStack);

        assert MinecraftClient.getInstance().currentScreen != null;
        ((NewWithdrawalScreen) MinecraftClient.getInstance().currentScreen).validate();
    }

}
