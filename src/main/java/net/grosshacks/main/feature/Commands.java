package net.grosshacks.main.feature;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Commands {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final List<String> chats = List.of("g", "l", "wc", "tr", "lfg", "gc");

    public static void register() {
        registerShowcase("show", "<mainhand>");
        registerShowcase("mainhand", "<mainhand>");
        registerShowcase("equipment", "<equipment>");
        registerShowcase("charms", "<charms>");
    }

    public static void registerShowcase(String base, String message) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal(base).executes(ctx -> showCommand(null, message))
                        .then(argument("chat", StringArgumentType.string())
                                .suggests((ctx, builder) -> getChatSuggestions(builder))
                                .executes(ctx -> showCommand(StringArgumentType.getString(ctx, "chat"), message)))
        ));
    }

    @SuppressWarnings("SameReturnValue")
    private static int showCommand(String chat, String message) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            if (chat == null) player.networkHandler.sendChatMessage(message);
            else player.networkHandler.sendCommand(chat + " " + message);
        }
        return 1;
    }

    private static CompletableFuture<Suggestions> getChatSuggestions(SuggestionsBuilder builder) {
        for (String chat : chats) builder.suggest(chat);
        return builder.buildFuture();
    }
}
