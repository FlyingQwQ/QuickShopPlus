package top.kuoer.command;

import org.bukkit.command.CommandSender;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;

public class ReloadCommand implements SimpleCommand{

    private final QuickShopPlus quickShopPlus;
    private final String permission;
    private final boolean isConsole;
    private final ShopType shopType;



    public ReloadCommand(QuickShopPlus quickShopPlus, String permission, ShopType shopType, boolean isConsole) {
        this.quickShopPlus = quickShopPlus;
        this.permission = permission;
        this.isConsole = isConsole;
        this.shopType = shopType;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        this.quickShopPlus.shoppingDefaultConfig.reloadConfig();
        this.quickShopPlus.shoppingPlayerConfig.reloadConfig();
        this.quickShopPlus.buyingDefaultConfig.reloadConfig();
        this.quickShopPlus.buyingPlayerConfig.reloadConfig();
        this.quickShopPlus.timeResetConfig.reloadConfig();
        this.quickShopPlus.reloadConfig();

        this.quickShopPlus.limiterTimer.updateRunTime();

        sender.sendMessage("§a 配置重载成功。");
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
        return isConsole;
    }

}
