package top.kuoer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.economy.AbstractEconomy;
import org.maxgamer.quickshop.api.shop.Info;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import org.maxgamer.quickshop.shop.SimpleShopManager;

import java.util.UUID;

public class SimpleShopManagerPlus extends SimpleShopManager {

    private final QuickShopPlus quickShopPlus;
    private final QuickShop quickShop;

    public SimpleShopManagerPlus(QuickShop quickShop, QuickShopPlus quickShopPlus) {
        super(quickShop);
        this.quickShopPlus = quickShopPlus;
        this.quickShop = quickShop;
        Bukkit.getConsoleSender().sendMessage("[QuickShopPlus] §aSimpleShopManager替换成功。");
    }

    // 玩家聊天事件
    @Override
    public void handleChat(Player p, String msg) {
        if(!this.quickShopPlus.playerChatModifyLimitMap.containsKey(p)) {
            super.handleChat(p, msg);
        }
    }

    // 点击商店发送商品信息到公屏
    @Override
    public void sendShopInfo(Player p, Shop shop) {
        super.sendShopInfo(p, shop);

        if(shop.getShopType() == ShopType.SELLING) {
            int shoppingPlayerLimitNumber = this.quickShopPlus.getShoppingLimit().limitGetPlayer(p, shop.getLocation());
            if(shoppingPlayerLimitNumber != -255 && shoppingPlayerLimitNumber != -1) {
                p.sendMessage("§6你当前购买的商品§c存在限购§6，你还可以购买 §e" + shoppingPlayerLimitNumber + " §6次");
            }
        } else {
            int buyingPlayerLimitNumber = this.quickShopPlus.getBuyingLimit().limitGetPlayer(p, shop.getLocation());
            if(buyingPlayerLimitNumber != -255 && buyingPlayerLimitNumber != -1) {
                p.sendMessage("§6你当前购买的商品§c存在收购限制§6，你还可以出售 §e" + buyingPlayerLimitNumber + " §6次");
            }
        }
    }



    // 开始购买通知
    @Override
    public void actionSell(UUID seller, Inventory sellerInventory, AbstractEconomy eco, Info info, Shop shop, int amount) {
        Player player = this.quickShop.getServer().getPlayer(seller);

        int playerLimitNumber = this.quickShopPlus.getShoppingLimit().limitGetPlayer(player, shop.getLocation());
        if(playerLimitNumber == -255) {
            super.actionSell(seller, sellerInventory, eco, info, shop, amount);
            return;
        }
        if(playerLimitNumber == -1)  {
            super.actionSell(seller, sellerInventory, eco, info, shop, amount);
            return;
        }
        if(playerLimitNumber > 0) {
            if((playerLimitNumber - amount) > -1) {
                super.actionSell(seller, sellerInventory, eco, info, shop, amount);
            } else {
                player.sendMessage("");
                player.sendMessage("§e" + amount + " §6个太多了，你最多还可以购买 §e" + playerLimitNumber + " §6个。");
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§c购买超过上限已经无法购买了");
        }
    }

    // 购买完成通知
    @Override
    public void sendPurchaseSuccess(UUID purchaser, Shop shop, int amount, double total) {
        super.sendPurchaseSuccess(purchaser, shop, amount, total);
        if(this.quickShopPlus.isShopExist(shop.getLocation())) {
            Player player = this.quickShop.getServer().getPlayer(purchaser);
            int playerLimitNumber = this.quickShopPlus.getShoppingLimit().limitGetPlayer(player, shop.getLocation());
            if(playerLimitNumber != -255) {
                this.quickShopPlus.getShoppingLimit().limitReducePlayer(player, shop.getLocation(), amount);
            }
        }
    }


    //  开始收购通知
    @Override
    public void actionBuy(UUID buyer, Inventory buyerInventory, AbstractEconomy eco, Info info, Shop shop, int amount) {
        Player player = this.quickShop.getServer().getPlayer(buyer);
        int playerLimitNumber = this.quickShopPlus.getBuyingLimit().limitGetPlayer(player, shop.getLocation());
        if(playerLimitNumber == -255) {
            super.actionBuy(buyer, buyerInventory, eco, info, shop, amount);
            return;
        }
        if(playerLimitNumber == -1)  {
            super.actionBuy(buyer, buyerInventory, eco, info, shop, amount);
            return;
        }

        if(playerLimitNumber > 0) {
            if((playerLimitNumber - amount) > -1) {
                super.actionBuy(buyer, buyerInventory, eco, info, shop, amount);
            } else {
                player.sendMessage("");
                player.sendMessage("§e" + amount + " §6个太多了，你最多还可以出售 §e" + playerLimitNumber + " §6个。");
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§c出售超过上限已经无法出售了");
        }

    }

    // 收购完成通知
    @Override
    public void sendSellSuccess(UUID seller, Shop shop, int amount, double total) {
        super.sendSellSuccess(seller, shop, amount, total);
        if(this.quickShopPlus.isShopExist(shop.getLocation())) {
            Player player = this.quickShop.getServer().getPlayer(seller);
            int playerLimitNumber = this.quickShopPlus.getBuyingLimit().limitGetPlayer(player, shop.getLocation());
            if(playerLimitNumber != -255) {
                this.quickShopPlus.getBuyingLimit().limitReducePlayer(player, shop.getLocation(), amount);
            }
        }
    }



    // 删除商店通知
    @Override
    public void removeShop(Shop shop) {
        if(this.quickShopPlus.isShopExist(shop.getLocation())) {
            if(shop.getShopType() == ShopType.SELLING) {
                this.quickShopPlus.getShoppingLimit().limitDel(shop.getLocation());
            } else {
                this.quickShopPlus.getBuyingLimit().limitDel(shop.getLocation());
            }
        }
        super.removeShop(shop);
    }

}
