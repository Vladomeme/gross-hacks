package net.grosshacks.main.wallet;

import net.grosshacks.main.util.Colours;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class NewWithdrawalScreen extends HandledScreen<NewWithdrawalScreenHandler> {

    private TextFieldWidget leftAmountField;
    private TextFieldWidget rightAmountField;
    private TextButtonWidget saveButton;

    public static final TextRenderer tr = MinecraftClient.getInstance().textRenderer;

    public NewWithdrawalScreen(PlayerInventory inventory) {
        super(new NewWithdrawalScreenHandler(0, inventory), inventory, Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        addDrawableChild(leftAmountField = new TextFieldWidget(tr, width / 2 - 67, height / 2 - 10, 30, 14, Text.empty()));
        leftAmountField.setChangedListener(s -> validate());

        addDrawableChild(rightAmountField = new TextFieldWidget(tr, width / 2 + 33, height / 2 - 10, 30, 14, Text.empty()));
        rightAmountField.setChangedListener(s -> validate());

        addDrawableChild(saveButton = new TextButtonWidget(TextButtonWidget.Type.Normal, width / 2 - 15, height / 2 + 22,
                30, 11, "Save", button -> save()));
        saveButton.active = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.getMatrices().push();
        context.getMatrices().scale(1.25f, 1.25f, 1);
        context.getMatrices().translate(0, 0, 1);
        context.drawCenteredTextWithShadow(tr, Text.of("New wallet withdrawal"),
                (int) (width / 2.5f), height / 2 - 120, Colours.text());
        context.getMatrices().pop();

        context.drawText(tr, "Left/right click to set item from inventory", width / 2 - tr.getWidth("Left/right click to set item from inventory") / 2,
                height / 2 - 60, Colours.text(), true);

        context.drawText(tr, "Item 1", width / 2 - 69 - tr.getWidth("Item 1"),
                height / 2 - 36, Colours.text(), true);
        context.drawText(tr, "Item 2", width / 2 + 31 - tr.getWidth("Item 2"),
                height / 2 - 36, Colours.text(), true);

        context.drawText(tr, "Amount", width / 2 - 71 - tr.getWidth("Amount"),
                height / 2 - 8, Colours.text(), true);

        context.drawBorder(width / 2 - 63, height / 2 - 43, 22, 22, Colours.border());
        context.drawBorder(width / 2 + 37, height / 2 - 43, 22, 22, Colours.border());

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public void validate() {
        ItemStack left = getScreenHandler().slots.get(0).getStack();
        ItemStack right = getScreenHandler().slots.get(1).getStack();
        if (left == ItemStack.EMPTY && right == ItemStack.EMPTY) {
            saveButton.active = false;
            return;
        }
        try {
            if (left != ItemStack.EMPTY) {
                int i = Integer.parseInt(leftAmountField.getText());
                if (i < 1 || i > 64) {
                    saveButton.active = false;
                    return;
                }
            }
            if (right != ItemStack.EMPTY) {
                int i = Integer.parseInt(leftAmountField.getText());
                if (i < 1 || i > 64) {
                    saveButton.active = false;
                    return;
                }
            }
        }
        catch (NumberFormatException e) {
            saveButton.active = false;
            return;
        }
        saveButton.active = true;
    }

    private void save() {
        ItemStack left = getScreenHandler().slots.get(0).getStack().copy();
        if (left == ItemStack.EMPTY) left = null;
        else left.setCount(Integer.parseInt(leftAmountField.getText()));

        ItemStack right = getScreenHandler().slots.get(1).getStack().copy();
        if (right == ItemStack.EMPTY) right = null;
        else right.setCount(Integer.parseInt(rightAmountField.getText()));

        if (left == null && right != null) {
            left = right;
            right = null;
        }
        WithdrawalIO.shouldSave = true;
        WalletManager.entries.add(new Withdrawal(left, right));
        MinecraftClient.getInstance().setScreen(null);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("New withdrawal added."));
    }

    @SuppressWarnings({"deprecation", "DataFlowIssue"})
    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot == null) return;
        if (button <= 1) {
            if (slot.inventory.size() == 2) {
                slot.setStack(ItemStack.EMPTY);
                validate();
            }
            else if (slot.getStack() != null && slot.getStack() != ItemStack.EMPTY) {
                ComponentMap components = slot.getStack().getComponents();
                if (!components.contains(DataComponentTypes.CUSTOM_DATA)) return;

                NbtCompound nbt = components.get(DataComponentTypes.CUSTOM_DATA).getNbt();

                if (nbt != null && nbt.getCompound("Monumenta").getString("Tier").equals("currency")) {
                    (button == 0 ? leftAmountField : rightAmountField).setText("1");
                    getScreenHandler().setSlot(button, slot.getStack().copyWithCount(1));
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        else if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        else {
            this.handleHotbarKeyPressed(keyCode, scanCode);
            if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
                if (client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
                    this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
                }
                else if (client.options.dropKey.matchesKey(keyCode, scanCode)) {
                    this.onMouseClick(this.focusedSlot, this.focusedSlot.id, hasControlDown() ? 1 : 0, SlotActionType.THROW);
                }
            }
            return true;
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

    }
}
