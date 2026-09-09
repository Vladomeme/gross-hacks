package net.grosshacks.main.mixin.buttons;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TexturedButtonWidget.class)
public abstract class TexturedButtonWidgetMixin extends ButtonWidget {

	@WrapOperation(method = "renderWidget", at = @At(value = "INVOKE",
			target="Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void gh$renderWidget(DrawContext context, Identifier id, int x, int y, int width, int height, Operation<Void> original) {
		if (id.getPath().startsWith("dynamic")) context.drawTexture(id, x, y, 0, 0, width, height, width, height);
		else original.call(context, id, x, y, width, height);
	}

	@SuppressWarnings("unused")
    protected TexturedButtonWidgetMixin(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
		super(x, y, width, height, message, onPress, narrationSupplier);
	}
}
