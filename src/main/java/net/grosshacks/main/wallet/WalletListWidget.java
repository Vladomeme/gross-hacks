package net.grosshacks.main.wallet;

import net.grosshacks.main.util.Colours;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class WalletListWidget extends ElementListWidget<WalletListEntry> {

    private boolean scrolling;
    public final int listWidth;

    public WalletListWidget(int left, int width, int height, int y) {
        super(MinecraftClient.getInstance(), width, height, y, 24);

        listWidth = width;
        setX(left);
        setEntries();

        setRenderHeader(false, 0);
    }

    public void setEntries() {
        clearEntries();

        List<Withdrawal> list = WalletManager.entries;
        if (list.isEmpty()) return;

        if (list.size() == 1) {
            addEntry(new WalletListEntry(list.getFirst(), WalletListEntry.Pos.Only));
            return;
        }
        addEntry(new WalletListEntry(list.getFirst(), WalletListEntry.Pos.Top));
        for (int i = 1; i < list.size() - 1; i++) {
            addEntry(new WalletListEntry(list.get(i), WalletListEntry.Pos.Middle));
        }
        addEntry(new WalletListEntry(list.getLast(), WalletListEntry.Pos.Bottom));

    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawBorder(getX() - 1, getY(), getWidth() + 2, getHeight() + 100, Colours.border());
        context.fill(getX(), getY(), getX() + getWidth() + 1, getY() + getHeight() + 100, Colours.backgroundAlt());
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected int getScrollbarX() {
        return getX() + getWidth() + 2;
    }

    @Override
    public int getRowWidth() {
        return listWidth;
    }

    @Override
    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        scrolling = button == 0 && mouseX >= (double) getScrollbarX() && mouseX < (double) (getScrollbarX() + 6);
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        updateScrollingState(mouseX, mouseY, button);
        if (isMouseOver(mouseX, mouseY)) {
            WalletListEntry entry = getEntryAtPosition(mouseX, mouseY);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    WalletListEntry entry2 = getFocused();
                    if (entry2 != entry && entry2 != null) ((ParentElement) entry2).setFocused(null);
                    setFocused(entry);
                    setDragging(true);
                    return true;
                }
                return scrolling;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        else if (button == 0 && scrolling) {
            if (mouseY < (double) getY()) setScrollAmount(0.0);
            else if (mouseY > (double)this.getBottom()) setScrollAmount(getMaxScroll());
            else {
                double d = Math.max(1, this.getMaxScroll());
                int i = this.height;
                int j = MathHelper.clamp((int) ((float) (i * i) / (float) getMaxPosition()), 32, i - 8);
                double e = Math.max(1.0, d / (double) (i - j));
                setScrollAmount(getScrollAmount() + deltaY * e);
            }
            return true;
        }
        else return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.getY() - 20 && mouseY <= (double)this.getBottom() + 10
                && mouseX >= (double)this.getX() - 10 && mouseX <= (double)this.getRight() + 10;
    }
}
