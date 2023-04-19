package top.kuoer.command;

import org.bukkit.command.CommandSender;
import org.maxgamer.quickshop.api.shop.ShopType;

public interface SimpleCommand {

    void run(CommandSender sender, String[] args);

    boolean isConsole();

    String getPermission();

    ShopType getShopType();
}
