package net.grosshacks.main.mixin.buttons;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.feature.GeneratedTextures;
import net.grosshacks.main.feature.wallet.WalletManager;
import net.grosshacks.main.util.Colours;
import net.grosshacks.main.util.MixinUtil;
import net.grosshacks.main.feature.wallet.NewWithdrawalScreen;
import net.grosshacks.main.feature.wallet.WalletListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.grosshacks.main.feature.wallet.WalletManager.*;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider, MixinUtil {

	@Unique private final MinecraftClient client = MinecraftClient.getInstance();

	@Unique ClientPlayNetworkHandler nh;
	@Unique TexturedButtonWidget statsButton;
	@Unique TexturedButtonWidget charmsButton;
	@Unique ButtonWidget dailiesButton;
	@Unique ButtonWidget walletWidget;
	@Unique public WalletListWidget walletListWidget;
	@SuppressWarnings("unused")
    @Unique TexturedButtonWidget addButton;

	@Unique final ItemStack DAILIES_DEFAULT_ITEM = Items.CLOCK.getDefaultStack();
	@Unique final ItemStack DAILIES_RP_ITEM = getRPItem(Items.MUSIC_DISC_11.getDefaultStack(), "Annilys", "fe1b8b10-fd00-4ba9-80a2-4a8a798ac1b2", 1);

	@Unique final ItemStack WALLET_DEFAULT_ITEM = Items.BUNDLE.getDefaultStack();
	@Unique final ItemStack WALLET_RP_ITEM = getRPItem(Items.FLOWER_POT.getDefaultStack(), "Bag of Hoarding", null, 1);

	@SuppressWarnings("DataFlowIssue")
    @Inject(method = "init", at = @At(value = "INVOKE",
			target="Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	private void gh$init(CallbackInfo ci) {
		nh = client.getNetworkHandler();
		if (GrossHacksConfig.INSTANCE.extraButtons) {
			addDrawableChild(statsButton = new TexturedButtonWidget(x + 126, height / 2 - 22, 20, 18,
					GeneratedTextures.statsTextures, button -> nh.sendCommand("ps")));
			addDrawableChild(charmsButton = new TexturedButtonWidget(x + 148, height / 2 - 22, 20, 18,
					GeneratedTextures.charmsTextures, button -> nh.sendCommand("vc")));
		}
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			addDrawableChild(dailiesButton = ButtonWidget.builder(Text.empty(), button -> nh.sendCommand("player status dailies"))
					.dimensions(x + 156, height / 2 - 103, 20, 20).build());
		}
		if (GrossHacksConfig.INSTANCE.withdrawMenu) {
			WalletManager.checkWallet();
			addDrawableChild(walletWidget = ButtonWidget.builder(Text.empty(), button -> {})
					.dimensions(5, height / 2 - 83, 20, 20).build());
			addDrawableChild(walletListWidget = new WalletListWidget(5, 90, 250, height / 2 - 83));
			walletListWidget.visible = false;

			ButtonTextures texture = new ButtonTextures(Identifier.of("grosshacks", "new"),
					Identifier.of("grosshacks", "new"));
			addDrawableChild(addButton = new TexturedButtonWidget(79, height / 2 - 99, 12, 12, texture,
					button -> client.setScreen(new NewWithdrawalScreen(client.player.getInventory()))));
			addButton.visible = false;
		}
	}

	@Inject(method = "method_19891(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("TAIL"))
	private void gh$moveButtonWithRecipeBook(CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.extraButtons) {
			statsButton.setPosition(x + 126, height / 2 - 22);
			charmsButton.setPosition(x + 148, height / 2 - 22);
		}
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			dailiesButton.setPosition(x + 156, height / 2 - 103);
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void gh$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			if (GrossHacks.citResewnLoaded)
				context.drawItem(DAILIES_RP_ITEM, dailiesButton.getX() + 2, dailiesButton.getY() + 2);
			else context.drawItem(DAILIES_DEFAULT_ITEM, dailiesButton.getX() + 2, dailiesButton.getY() + 2);

			if (dailiesButton.isMouseOver(mouseX, mouseY))
				context.drawTooltip(client.textRenderer, Text.of("Dailies"), mouseX, mouseY);
		}
		if (GrossHacksConfig.INSTANCE.withdrawMenu) {
			if (walletWidget.visible) {
				if (GrossHacks.citResewnLoaded) context.drawItem(WALLET_RP_ITEM, walletWidget.getX() + 2, walletWidget.getY() + 2);
				else context.drawItem(WALLET_DEFAULT_ITEM, walletWidget.getX() + 2, walletWidget.getY() + 2);
			}
			if (walletWidget.isMouseOver(mouseX, mouseY)) {
				if (isWalletAvailable()) {
					walletWidget.visible = false;
					walletListWidget.visible = true;
					addButton.visible = true;
				}
				else context.drawTooltip(client.textRenderer, Text.of("No wallet in inventory!"), mouseX, mouseY);
			}
			if (walletListWidget.visible) {
				context.drawText(client.textRenderer, "Withdrawals",
						walletListWidget.getX() + 3, walletListWidget.getY() - 14, Colours.text(), true);
				if (!walletListWidget.isMouseOver(mouseX, mouseY)) {
					walletWidget.visible = true;
					walletListWidget.visible = false;
					addButton.visible = false;
				}
				else if (addButton.isMouseOver(mouseX, mouseY)) {
					context.getMatrices().push();
					context.getMatrices().translate(0, 0, 1000);
					context.drawTooltip(client.textRenderer, Text.of("Add"), mouseX, mouseY);
					context.getMatrices().pop();
				}
			}
		}
	}

	@Override
	public void gh$updateEntries() {
		walletListWidget.setEntries();
	}

	@SuppressWarnings("unused")
	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
}
