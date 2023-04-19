package top.kuoer.command;

import org.bukkit.command.CommandSender;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;

public class HelpCommand implements SimpleCommand {

    private final QuickShopPlus quickShopPlus;
    private final String permission;
    private final boolean isConsole;
    private final ShopType shopType;


    public HelpCommand(QuickShopPlus quickShopPlus, String permission, ShopType shopType, boolean isConsole) {
        this.quickShopPlus = quickShopPlus;
        this.permission = permission;
        this.isConsole = isConsole;
        this.shopType = shopType;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage("");
        sender.sendMessage("§aQuickShopPlus 帮助");
        sender.sendMessage("§aTips: 出现§7[§c*§7]§a需要对着箱子商店输入命令");
        sender.sendMessage("§a/qsp help §e- 显示QuickShopPlus帮助");
        sender.sendMessage("§a/qsp reload §e- 重载插件配置");
        sender.sendMessage("§a/qsp rstime §e- 取消对这个商店的自动重置 §7[§c*§7]");
        sender.sendMessage("");
        sender.sendMessage("§a/qsp srs <玩家名> §e- 重置玩家对§b这个商店§e的限购 §7[§c*§7]");
        sender.sendMessage("§a/qsp srsall §e- 重置所有玩家§b对这个商店§e的限购 §7[§c*§7]");
        sender.sendMessage("§a/qsp smod <玩家名> <限购数量> §e- 修改玩家对§b这个商店§e的限购 §7[§c*§7]");
        sender.sendMessage("§a/qsp srem <玩家名> §e- 解除玩家对§b这个商店§e的限购 §7[§c*§7]");
        sender.sendMessage("§a/qsp sdel §e- 解除§b这个商店§e的限购 §7[§c*§7]");
        sender.sendMessage("");
        sender.sendMessage("§a/qsp brs <玩家名> §e- 重置玩家对§b这个商店§e的出售限制 §7[§c*§7]");
        sender.sendMessage("§a/qsp brsall §e- 重置所有玩家§b对这个商店§e的出售限制 §7[§c*§7]");
        sender.sendMessage("§a/qsp bmod <玩家名> <限购数量> §e- 修改玩家对§b这个商店§e的出售限制 §7[§c*§7]");
        sender.sendMessage("§a/qsp brem <玩家名> §e- 解除玩家对§b这个商店§e的出售限制 §7[§c*§7]");
        sender.sendMessage("§a/qsp bdel §e- 解除§b这个商店§e的出售限制 §7[§c*§7]");
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
