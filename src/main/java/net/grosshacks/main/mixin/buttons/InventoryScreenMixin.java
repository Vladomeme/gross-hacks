package net.grosshacks.main.mixin.buttons;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

	@Unique ClientPlayNetworkHandler nh;
	@Unique TexturedButtonWidget statsButton;
	@Unique TexturedButtonWidget charmsButton;
	@Unique ButtonWidget dailiesButton;
	@Unique ItemStack DEFAULT_ITEM = Items.CLOCK.getDefaultStack();
	@Unique ItemStack RP_ITEM = getRPItem();
	@Unique Identifier ID = Identifier.of("minecraft", "optifine/cit/celsian_isles/skin/patron/depefs_annilys/depefs_annilys.png");

	@Inject(method = "init", at = @At(value = "INVOKE",
			target="Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	private void init(CallbackInfo ci) {
		nh = MinecraftClient.getInstance().getNetworkHandler();
		if (GrossHacksConfig.INSTANCE.extraButtons) {
			addDrawableChild(statsButton = new TexturedButtonWidget(x + 126, height / 2 - 22, 20, 18,
					GrossHacks.stats, button -> nh.sendCommand("ps")));
			addDrawableChild(charmsButton = new TexturedButtonWidget(x + 148, height / 2 - 22, 20, 18,
					GrossHacks.charms, button -> nh.sendCommand("vc")));
		}
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			addDrawableChild(dailiesButton = ButtonWidget.builder(Text.empty(), button -> nh.sendCommand("player status dailies"))
					.dimensions(x + 156, height / 2 - 103, 20, 20).build());
		}
	}

	@Inject(method = "method_19891(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("TAIL"))
	private void moveButtonWithRecipeBook(CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.extraButtons) {
			statsButton.setPosition(x + 126, height / 2 - 22);
			charmsButton.setPosition(x + 148, height / 2 - 22);
		}
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			dailiesButton.setPosition(x + 156, height / 2 - 103);
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.dailiesButton) {
			if (MinecraftClient.getInstance().getResourceManager().getResource(ID).isPresent())
				context.drawItem(RP_ITEM, dailiesButton.getX() + 2, dailiesButton.getY() + 2);
			else context.drawItem(DEFAULT_ITEM, dailiesButton.getX() + 2, dailiesButton.getY() + 2);

			if (dailiesButton.isMouseOver(mouseX, mouseY))
				context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Dailies"), mouseX, mouseY);
		}
	}

	@Unique
	private ItemStack getRPItem() {
		ItemStack stack = Items.MUSIC_DISC_11.getDefaultStack();

		NbtCompound nbt = new NbtCompound();
		NbtCompound plain = new NbtCompound();
		nbt.put("plain", plain);
		NbtCompound display = new NbtCompound();
		plain.put("display", display);
		display.putString("Name", "Annilys");

		NbtCompound monumenta = new NbtCompound();
		nbt.put("Monumenta", monumenta);
		NbtCompound playerModified = new NbtCompound();
		monumenta.put("PlayerModified", playerModified);
		NbtCompound infusions = new NbtCompound();
		playerModified.put("Infusions", infusions);
		NbtCompound hope = new NbtCompound();
		infusions.put("Hope", hope);
		hope.putString("Infuser", "fe1b8b10-fd00-4ba9-80a2-4a8a798ac1b2");

		GrossHacks.LOGGER.info(nbt.asString());
		stack.setNbt(nbt);
		return stack;
	}

	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}
}
