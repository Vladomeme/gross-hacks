package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

	@Unique
	TexturedButtonWidget statsButton;
	@Unique
	TexturedButtonWidget charmsButton;

	@Inject(method = "init", at = @At(value = "INVOKE",
			target="Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	private void init(CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.enable_extra_buttons) {
			if (GrossHacks.stats == null) {
				GrossHacks.stats = new Identifier("grosshacks", "textures/stats_button.png");
				GrossHacks.charms = new Identifier("grosshacks", "textures/charms_button.png");
			}
			this.addDrawableChild(statsButton = new TexturedButtonWidget(this.x + 126, this.height / 2 - 22, 20, 18, 0, 0, 19,
					GrossHacks.stats, button -> MinecraftClient.getInstance().getNetworkHandler().sendCommand("ps")));
			this.addDrawableChild(charmsButton = new TexturedButtonWidget(this.x + 148, this.height / 2 - 22, 20, 18, 0, 0, 19,
					GrossHacks.charms, button -> MinecraftClient.getInstance().getNetworkHandler().sendCommand("vc")));
		}
	}

	@Inject(method = "method_19891(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("TAIL"))
	private void moveButtonWithRecipeBook(CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.enable_extra_buttons) {
			this.statsButton.setPosition(this.x + 126, this.height / 2 - 22);
			this.charmsButton.setPosition(this.x + 148, this.height / 2 - 22);
		}
	}

	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
}
