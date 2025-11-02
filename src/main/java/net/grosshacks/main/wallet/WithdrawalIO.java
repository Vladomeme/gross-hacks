package net.grosshacks.main.wallet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.GrossHacks;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WithdrawalIO {

    static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "grosshacks_wallet.json");
    static boolean shouldSave = false;

    public static List<Withdrawal> read() {
        if (!FILE.exists()) return new ArrayList<>();
        try {
            List<ProtoWithdrawal> protoList = new Gson().fromJson(Files.readString(FILE.toPath(), StandardCharsets.UTF_8),
                    new TypeToken<List<ProtoWithdrawal>>(){}.getType());
            return protoList.stream().map(Withdrawal::fromProto).collect(Collectors.toCollection(ArrayList::new));
        }
        catch (Exception e) {
            GrossHacks.LOGGER.error("Couldn't read wallet withdrawals list");
            throw new RuntimeException(e);
        }
    }

    public static void write() {
        if (!shouldSave) return;
        shouldSave = false;

        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            writer = gson.newJsonWriter(new FileWriter(FILE, StandardCharsets.UTF_8));
            writer.setIndent("    ");
            List<ProtoWithdrawal> list = new ArrayList<>(WalletManager.entries.size());
            for (Withdrawal withdrawal : WalletManager.entries) {
                String itemTypeLeft = withdrawal.left().getItem().toString().split(":")[1];
                String itemNameLeft = Withdrawal.getName(withdrawal.left());
                int countLeft = withdrawal.left().getCount();
                if (withdrawal.right() == null || withdrawal.right() == ItemStack.EMPTY) {
                    list.add(new ProtoWithdrawal(itemTypeLeft, itemNameLeft, countLeft, null, null, 0));
                    continue;
                }
                String itemTypeRight = withdrawal.right().getItem().toString().split(":")[1];
                String itemNameRight = Withdrawal.getName(withdrawal.right());
                int countRight = withdrawal.right().getCount();
                list.add(new ProtoWithdrawal(itemTypeLeft, itemNameLeft, countLeft, itemTypeRight, itemNameRight, countRight));
            }
            gson.toJson(gson.toJsonTree(list, new TypeToken<List<ProtoWithdrawal>>(){}.getType()), writer);
        }
        catch (Exception e) {
            GrossHacks.LOGGER.error("Couldn't save wallet withdrawals list");
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public record ProtoWithdrawal(String itemTypeLeft, String itemNameLeft, int countLeft, String itemTypeRight, String itemNameRight, int countRight) {

    }
}
