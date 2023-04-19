package top.kuoer.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;

import java.util.ArrayList;
import java.util.List;

public class TimeResetCommand implements SimpleCommand {

    private final QuickShopPlus quickShopPlus;
    private final String permission;
    private final boolean isConsole;
    private final ShopType shopType;

    public TimeResetCommand(QuickShopPlus quickShopPlus, String permission, ShopType shopType, boolean isConsole) {
        this.quickShopPlus = quickShopPlus;
        this.permission = permission;
        this.isConsole = isConsole;
        this.shopType = shopType;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Shop shop = this.quickShopPlus.getPointerShop(sender, true);
        if(null != shop) {
            if(!sender.hasPermission("quickshopplus.others")) {
                if(!shop.getOwner().equals(((Player) sender).getUniqueId())) {
                    sender.sendMessage("§c你没有权限对他人商店进行修改");
                    return;
                }
            }

            this.quickShopPlus.getTimeReset().toggle((Player) sender, shop.getLocation());
        }
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public ShopType getShopType() {
        return this.shopType;
    }

    @Override
    public boolean isConsole() {
        return this.isConsole;
    }

}
