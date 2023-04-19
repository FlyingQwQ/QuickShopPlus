package top.kuoer.command.buying;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;
import top.kuoer.command.SimpleCommand;

public class BuyingDeleteCommand implements SimpleCommand {

    private final QuickShopPlus quickShopPlus;
    private final String permission;
    private final boolean isConsole;
    private final ShopType shopType;


    public BuyingDeleteCommand(QuickShopPlus quickShopPlus, String permission, ShopType shopType, boolean isConsole) {
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

            this.quickShopPlus.getBuyingLimit().limitDel(shop.getLocation());
            sender.sendMessage("§a已经将这个商店的收购限制移除了");
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
