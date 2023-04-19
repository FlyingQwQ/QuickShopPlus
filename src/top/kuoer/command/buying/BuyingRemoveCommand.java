package top.kuoer.command.buying;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;
import top.kuoer.command.SimpleCommand;

public class BuyingRemoveCommand implements SimpleCommand {

    private final QuickShopPlus quickShopPlus;
    private final String permission;
    private final boolean isConsole;
    private final ShopType shopType;


    public BuyingRemoveCommand(QuickShopPlus quickShopPlus, String permission, ShopType shopType, boolean isConsole) {
        this.quickShopPlus = quickShopPlus;
        this.permission = permission;
        this.isConsole = isConsole;
        this.shopType = shopType;
    }
    @Override
    public void run(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage("§a正确格式 §6/qsp brem <玩家名>");
            return;
        }

        Shop shop = this.quickShopPlus.getPointerShop(sender, true);
        if(null != shop) {
            if(!sender.hasPermission("quickshopplus.others")) {
                if(!shop.getOwner().equals(((Player) sender).getUniqueId())) {
                    sender.sendMessage("§c你没有权限对他人商店进行修改");
                    return;
                }
            }

            Player removePlayer = Bukkit.getServer().getPlayer(args[1]);
            if(null != removePlayer) {
                try {
                    this.quickShopPlus.getBuyingLimit().limitRemovePlayer(removePlayer, shop.getLocation());
                    sender.sendMessage("§a已经解除 §e" + args[1] + " §a对这个商店的收购限制");
                }catch(NumberFormatException evt) {
                    sender.sendMessage("§c您只能输入一个数字，您的输入是 " + args[2] + "。");
                }
            } else {
                sender.sendMessage("§c没有找到这个玩家");
            }
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
