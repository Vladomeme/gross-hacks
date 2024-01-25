package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.ChatBlocker;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements ChatBlocker {

	@Shadow public abstract void addMessage(Text message);

	@Shadow @Final
	private List<ChatHudLine> messages;

	@Unique
	boolean chatBlocked;
	@Unique
	boolean messageFound = false;
	@Unique
	ArrayList<Text> messageHistory = new ArrayList<>();

	//Block chat for a tick once an empty message is received (a sign that chat got refreshed)
	//Find first non-empty message
	//Copy all chat messages after it from chat history
	//Compare new sent messages with chat history
	//If deleted message is found, mark it and add back to chat
	//Unblock chat once history is clear
	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
			at = @At(value = "HEAD"), cancellable = true)
	private void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.undelete) {
			if (message.getString().isEmpty()) {
				chatBlocked = true;
				ci.cancel();
			} else if (chatBlocked) {
				if (!messageFound) {
					String firstMessage = message.getString();
					for (ChatHudLine line : messages) {
						messageHistory.add(line.content());
						if (line.content().getString().equals(firstMessage)) {
							messageFound = true;
							break;
						}
					}
				}
				if (messageHistory.isEmpty()) {
					chatBlocked = false;
					messageFound = false;
				} else {
					if (message.getString().equals(messageHistory.get(messageHistory.size() - 1).getString())) {
						send(message);
					} else {
						send(modifyDeletedMessage(messageHistory.get(messageHistory.size() - 1).copy()));
						send(message);
					}
				}
				ci.cancel();
			}
		}
	}

	@Unique
	private void send(Text message) {
		chatBlocked = false;
		addMessage(message);
		chatBlocked = true;
		messageHistory.remove(messageHistory.size() - 1);
	}

	@Unique
	public Text modifyDeletedMessage(MutableText message) {
		Text text = !message.getSiblings().isEmpty() ? message.getSiblings().get(message.getSiblings().size() - 1) : message;
		MutableText newMessage = Text.empty();

		if (!message.getSiblings().isEmpty()) {
			newMessage = Text.literal(message.asTruncatedString(getTrunkLength(message.copy())))
					.setStyle(message.getStyle().withStrikethrough(true));
			for (int i = 0; i < message.getSiblings().size() - 1; i++) {
				newMessage.append(message.getSiblings().get(i).copy()
						.setStyle(message.getSiblings().get(i).getStyle().withStrikethrough(true)));
			}
		}
		newMessage.append(text.copy().setStyle(text.getStyle().withStrikethrough(true)));
		newMessage.append(Text.literal(" [DELETED]")
				.setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.RED).withStrikethrough(false)));

		return newMessage;
	}

	@Unique
	public int getTrunkLength(MutableText text) {
		int length = 0;
		for (Text section : text.getSiblings()) {
			length += section.getString().length();
		}
		return text.getString().length() - length;
	}

	@Unique
	public boolean isBlocked() {
		return chatBlocked;
	}

	//Runs at the end of the tick if chat is blocked
	@Unique
	public void unblockChat() {
		chatBlocked = false;
		messageFound = false;
		for (Text message : messageHistory) {
			addMessage(message);
		}
		messageHistory.clear();
	}
}
