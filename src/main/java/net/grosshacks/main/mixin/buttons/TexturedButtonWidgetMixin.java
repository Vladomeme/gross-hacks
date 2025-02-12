package net.grosshacks.main.mixin.buttons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TexturedButtonWidget.class)
public abstract class TexturedButtonWidgetMixin extends ButtonWidget {

	@Redirect(method = "renderWidget", at = @At(value = "INVOKE",
			target="Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void renderWidget(DrawContext context, Identifier id, int x, int y, int width, int height) {
		if (id.getPath().charAt(0) == 'd') context.drawTexture(id, x, y, 0, 0, width, height, width, height);
		else context.drawGuiTexture(id, this.getX(), this.getY(), this.width, this.height);
	}

	protected TexturedButtonWidgetMixin(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
		super(x, y, width, height, message, onPress, narrationSupplier);
	}
}
