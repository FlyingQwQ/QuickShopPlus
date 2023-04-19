package top.kuoer.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;
import top.kuoer.command.buying.*;
import top.kuoer.command.shopping.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {

    private final QuickShopPlus quickShopPlus;
    private final Map<String, SimpleCommand> simpleCommandMap;

    public CommandManager(QuickShopPlus quickShopPlus) {
        this.quickShopPlus = quickShopPlus;
        this.simpleCommandMap = new HashMap<>();

        simpleCommandMap.put("help", new HelpCommand(quickShopPlus, "quickshopplus.help", null, true));
        simpleCommandMap.put("reload", new ReloadCommand(quickShopPlus, "quickshopplus.reload", null,true));
        simpleCommandMap.put("rstime", new TimeResetCommand(quickShopPlus, "quickshopplus.rstime", null,false));

        simpleCommandMap.put("srs", new ShoppingResetCommand(quickShopPlus, "quickshopplus.shopping.reset", ShopType.SELLING, false));
        simpleCommandMap.put("srsall", new ShoppingResetAllCommand(quickShopPlus, "quickshopplus.shopping.resetall", ShopType.SELLING, false));
        simpleCommandMap.put("smod", new ShoppingModifyCommand(quickShopPlus, "quickshopplus.shopping.modify", ShopType.SELLING, false));
        simpleCommandMap.put("srem", new ShoppingRemoveCommand(quickShopPlus, "quickshopplus.shopping.remove", ShopType.SELLING, false));
        simpleCommandMap.put("sdel", new ShoppingDeleteCommand(quickShopPlus, "quickshopplus.shopping.delete", ShopType.SELLING, false));

        simpleCommandMap.put("brs", new BuyingResetCommand(quickShopPlus, "quickshopplus.buying.reset", ShopType.BUYING, false));
        simpleCommandMap.put("brsall", new BuyingResetAllCommand(quickShopPlus, "quickshopplus.buying.resetall", ShopType.BUYING, false));
        simpleCommandMap.put("bmod", new BuyingModifyCommand(quickShopPlus, "quickshopplus.buying.modify", ShopType.BUYING, false));
        simpleCommandMap.put("brem", new BuyingRemoveCommand(quickShopPlus, "quickshopplus.buying.remove", ShopType.BUYING, false));
        simpleCommandMap.put("bdel", new BuyingDeleteCommand(quickShopPlus, "quickshopplus.buying.delete", ShopType.BUYING, false));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length > 0) {
            if(this.simpleCommandMap.containsKey(args[0])) {
                SimpleCommand simpleCommand = this.simpleCommandMap.get(args[0]);

                if(!simpleCommand.isConsole() && this.isConsole(sender)) {
                    sender.sendMessage("§c你不能在控制台使用该命令。");
                    return true;
                }

                if(null != simpleCommand.getShopType()) {
                    Shop targetShop = this.quickShopPlus.getPointerShop(sender, false);
                    if(null != targetShop) {
                        if(targetShop.getShopType() != simpleCommand.getShopType()) {
                            if(targetShop.getShopType() == ShopType.SELLING) {
                                sender.sendMessage("§c请使用关于限购的命令对商店进行限制修改");
                            } else {
                                sender.sendMessage("§c请使用关于收购限制的命令对商店进行限制修改");
                            }
                            return true;
                        }
                    }
                }

                if(!sender.hasPermission(simpleCommand.getPermission())) {
                    sender.sendMessage("§c你没有权限执行该命令。");
                    return true;
                }
                simpleCommand.run(sender, args);
                return true;
            }
        }
        if(this.simpleCommandMap.containsKey("help")) {
            this.simpleCommandMap.get("help").run(sender, args);
        } else {
            sender.sendMessage("§c没有注册帮助命令。");
        }
        return true;
    }


    /**
     * 判断命令发送者是不是控制台
     * @param sender 命令发送者
     * @return boolean
     */
    public boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }


}
