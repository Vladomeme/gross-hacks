package net.grosshacks.main.wallet;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.grosshacks.main.util.Colours;
import net.grosshacks.main.util.MixinUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class WalletListEntry extends ElementListWidget.Entry<WalletListEntry> {

    private final TextRenderer tr = MinecraftClient.getInstance().textRenderer;

    public final Withdrawal withdrawal;

    public final ButtonWidget withdrawButton;
    public final TexturedButtonWidget removeButton;
    public final TexturedButtonWidget upButton;
    public final TexturedButtonWidget downButton;
    public final List<ClickableWidget> list;

    public WalletListEntry(Withdrawal withdrawal, Pos pos) {
        this.withdrawal = withdrawal;

        withdrawButton = ButtonWidget.builder(Text.of(""), button -> withdrawal.withdraw()).dimensions(0, 0, 50, 20).build();

        ButtonTextures texture1 = new ButtonTextures(new Identifier("grosshacks", "remove"),
                new Identifier("grosshacks", "remove"));
        removeButton = new TexturedButtonWidget(0, 0, 12, 12, texture1, button -> {
            withdrawal.remove();
            ((MixinUtil) Objects.requireNonNull(MinecraftClient.getInstance().currentScreen)).gh$updateEntries();
        });
        ButtonTextures texture2 = new ButtonTextures(new Identifier("grosshacks", "up_unfocused"),
                new Identifier("grosshacks", "up_focused"));
        upButton = new TexturedButtonWidget(0, 0, 7, 4, texture2, button -> {
            WalletManager.moveUp(withdrawal);
            ((MixinUtil) Objects.requireNonNull(MinecraftClient.getInstance().currentScreen)).gh$updateEntries();
        });
        ButtonTextures texture3 = new ButtonTextures(new Identifier("grosshacks", "down_unfocused"),
                new Identifier("grosshacks", "down_focused"));
        downButton = new TexturedButtonWidget(0, 0, 7, 4, texture3, button -> {
            WalletManager.moveDown(withdrawal);
            ((MixinUtil) Objects.requireNonNull(MinecraftClient.getInstance().currentScreen)).gh$updateEntries();
        });
        setPos(pos);

        list = ImmutableList.of(withdrawButton, removeButton, upButton, downButton);
    }

    public void setPos(Pos pos) {
        switch (pos) {
            case Only -> {
                upButton.visible = false;
                downButton.visible = false;
            }
            case Top -> {
                upButton.visible = false;
                downButton.visible = true;
            }
            case Middle -> {
                upButton.visible = true;
                downButton.visible = true;
            }
            case Bottom -> {
                upButton.visible = true;
                downButton.visible = false;
            }
        }
    }

    @Override
    public void render(DrawContext context, int index, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        withdrawButton.setPosition(left, top);
        removeButton.setPosition(left + 73, top + 4);
        upButton.setPosition(left + 63, top + 5);
        downButton.setPosition(left + 63, top + 11);

        RenderSystem.enableBlend();
        context.drawBorder(left, top, 87, 21, Colours.border());
        context.fill(left, top, left + 86, top + 21, Colours.background(isMouseOver(mouseX, mouseY)));

        removeButton.render(context, mouseX, mouseY, tickDelta);
        upButton.render(context, mouseX, mouseY, tickDelta);
        downButton.render(context, mouseX, mouseY, tickDelta);

        ItemStack stackLeft = withdrawal.left();
        ItemStack stackRight = withdrawal.right();

        context.drawItem(stackLeft, left + 2, top + 2);
        context.drawItemInSlot(tr, stackLeft, left + 2, top + 2);

        if (stackRight != null) {
            context.drawItem(stackRight, left + 30, top + 2);
            context.drawItemInSlot(tr, stackRight, left + 30, top + 2);
        }

        RenderSystem.disableScissor();
        if (removeButton.isMouseOver(mouseX, mouseY))
            context.drawTooltip(tr, Text.of("Delete"), mouseX, mouseY);
        if (upButton.isMouseOver(mouseX, mouseY))
            context.drawTooltip(tr, Text.of("Move up"), mouseX, mouseY);
        if (downButton.isMouseOver(mouseX, mouseY))
            context.drawTooltip(tr, Text.of("Move down"), mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        withdrawButton.mouseClicked(mouseX, mouseY, button);
        removeButton.mouseClicked(mouseX, mouseY, button);
        upButton.mouseClicked(mouseX, mouseY, button);
        downButton.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public List<? extends Element> children() {
        return list;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return list;
    }

    public enum Pos {
        Only,
        Top,
        Middle,
        Bottom
    }
}
