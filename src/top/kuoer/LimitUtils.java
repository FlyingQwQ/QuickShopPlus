package top.kuoer;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.maxgamer.quickshop.QuickShop;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class LimitUtils {

    private QuickShopPlus quickShopPlus;
    private QuickShop quickShop;
    private ShopPlusConfig defaultConfig;
    private ShopPlusConfig playerConfig;

    public LimitUtils(QuickShopPlus quickShopPlus, QuickShop quickShop, ShopPlusConfig defaultConfig, ShopPlusConfig playerConfig) {
        this.quickShopPlus = quickShopPlus;
        this.quickShop = quickShop;
        this.defaultConfig = defaultConfig;
        this.playerConfig = playerConfig;
    }


    /**
     * 获得箱子商店默认限制数
     * @param location 箱子商店坐标
     * @return 限制数量
     */
    public int limitGetDefault(Location location) {
        String shopPos = this.quickShopPlus.locationTrans(location);

        List<String> shopposList = (List<String>) this.defaultConfig.getConfig().getList("shoppos");
        if(null != shopposList) {
            for (String item : shopposList) {
                if(item.substring(0, item.lastIndexOf(";")).equals(shopPos)) {
                    return Integer.parseInt(item.substring(item.lastIndexOf(";") + 1));
                }
            }
        }
        return -1;
    }

    /**
     * 修改箱子商店默认限数
     * @param location 箱子商店坐标
     * @param limitNumber 限制数量
     */
    public void limitSetDefault(Location location, int limitNumber) {
        String shopPos = this.quickShopPlus.locationTrans(location);
        String shopData = shopPos + ";" + limitNumber;

        List<String> shopposList = (List<String>) this.defaultConfig.getConfig().getList("shoppos");
        if(null == shopposList) {
            shopposList = new ArrayList<>();
        }

        int count = 0;
        if(shopposList.size() > 0) {
            for (int i = 0; i < shopposList.size(); i++) {
                String item = shopposList.get(i).substring(0, shopposList.get(i).lastIndexOf(";"));
                if(item.equals(shopPos)) {
                    shopposList.set(i, shopData);
                    count++;
                    break;
                }
            }
        }

        if(count < 1) {
            shopposList.add(shopData);
        }

        this.defaultConfig.getConfig().set("shoppos", shopposList);
        this.defaultConfig.saveConfig();
    }

    /**
     * 删除这个箱子商店的限制
     * @param player 玩家
     * @param location  箱子商店坐标
     */
    public void limitDel(Location location) {
        String shopPos = this.quickShopPlus.locationTrans(location);

        List<String> shopposList = (List<String>) this.defaultConfig.getConfig().getList("shoppos");
        ConfigurationSection players = this.playerConfig.getConfig().getConfigurationSection("players");
        if(null != players) {
            List<String> playerNames = players.getKeys(true).stream().toList();
            for (int index = 0; index < playerNames.size(); index++) {
                List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerNames.get(index));
                if(null != playerShopPosList) {
                    for (int i = 0; i < playerShopPosList.size(); i++) {
                        String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
                        if(item.equals(shopPos)) {
                            playerShopPosList.remove(i);
                            this.playerConfig.getConfig().set("players." + playerNames.get(index), playerShopPosList);
                            break;
                        }
                    }
                }
            }
            this.playerConfig.saveConfig();
        }

        if(null != shopposList && shopposList.size() > 0) {
            for (int i = 0; i < shopposList.size(); i++) {
                String item = shopposList.get(i).substring(0, shopposList.get(i).lastIndexOf(";"));
                if(item.equals(shopPos)) {
                    shopposList.remove(i);
                    this.defaultConfig.getConfig().set("shoppos", shopposList);
                    break;
                }
            }
            this.defaultConfig.saveConfig();
        }
    }

    /**
     * 获取玩家剩余的限制数量
     * @param player 玩家
     * @param location 箱子商店坐标
     * @return 剩余限制数量
     */
    public int limitGetPlayer(Player player, Location location) {
        String playerName = player.getName();
        String shopPos = this.quickShopPlus.locationTrans(location);

        List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerName);
        if(null != playerShopPosList && playerShopPosList.size() > 0) {
            for (int i = 0; i < playerShopPosList.size(); i++) {
                String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
                if(item.equals(shopPos)) {
                    if(!this.quickShopPlus.isShopExist(location)) {
                        playerShopPosList.remove(i);
                        this.playerConfig.getConfig().set("players." + playerName, playerShopPosList);
                        this.playerConfig.saveConfig();
                        return -1;
                    }
                    return Integer.parseInt(playerShopPosList.get(i).substring(playerShopPosList.get(i).lastIndexOf(";") + 1));
                }
            }
        }

        return this.limitGetDefault(location);
    }

    /**
     * 减少玩家能购买的限制数量
     * @param location  箱子商店的坐标
     */
    public void limitReducePlayer(Player player, Location location, int quantity) {
        String playerName = player.getName();
        List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerName);
        String shopPos = this.quickShopPlus.locationTrans(location);

        int count = 0;
        if(null == playerShopPosList) {
            playerShopPosList = new ArrayList<>();
        }
        for (int i = 0; i < playerShopPosList.size(); i++) {
            String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
            if(item.equals(shopPos)) {
                int number = Integer.parseInt(playerShopPosList.get(i).substring(playerShopPosList.get(i).lastIndexOf(";") + 1));
                playerShopPosList.set(i, item + ";" + (number - quantity));
                count++;
                break;
            }
        }

        if(count < 1) {
            playerShopPosList.add(shopPos + ";" + (this.limitGetDefault(location) - quantity));
        }

        this.playerConfig.getConfig().set("players." + playerName, playerShopPosList);
        this.playerConfig.saveConfig();
    }

    /**
     * 设置玩家能购买的限制数量
     * @param player 玩家
     * @param location 箱子商店的坐标
     * @param quantity 设置的值
     */
    public void limitSetPlayer(Player player, Location location, int quantity) {
        String playerName = player.getName();
        List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerName);
        String shopPos = this.quickShopPlus.locationTrans(location);

        int count = 0;
        if(null == playerShopPosList) {
            playerShopPosList = new ArrayList<>();
        }
        for (int i = 0; i < playerShopPosList.size(); i++) {
            String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
            if(item.equals(shopPos)) {
                playerShopPosList.set(i, item + ";" + quantity);
                count++;
                break;
            }
        }

        if(count < 1) {
            playerShopPosList.add(shopPos + ";" + quantity);
        }

        this.playerConfig.getConfig().set("players." + playerName, playerShopPosList);
        this.playerConfig.saveConfig();
    }

    /**
     * 重置玩家指定商店的默认值
     * @param player 玩家
     * @param location 箱子商店的坐标
     */
    public void limitResetPlayerTheDefault(Player player, Location location) {
        String playerName = player.getName();
        List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerName);
        String shopPos = this.quickShopPlus.locationTrans(location);

        int count = 0;
        if(null == playerShopPosList) {
            playerShopPosList = new ArrayList<>();
        }
        for (int i = 0; i < playerShopPosList.size(); i++) {
            String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
            if(item.equals(shopPos)) {
                playerShopPosList.set(i, item + ";" + this.limitGetDefault(location));
                count++;
                break;
            }
        }

        if(count < 1) {
            playerShopPosList.add(shopPos + ";" + (this.limitGetDefault(location)));
        }

        this.playerConfig.getConfig().set("players." + playerName, playerShopPosList);
        this.playerConfig.saveConfig();
    }
    public void limitResetPlayerTheDefault(String playerName, Location location) {
        List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerName);
        String shopPos = this.quickShopPlus.locationTrans(location);

        int count = 0;
        if(null == playerShopPosList) {
            playerShopPosList = new ArrayList<>();
        }
        for (int i = 0; i < playerShopPosList.size(); i++) {
            String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
            if(item.equals(shopPos)) {
                playerShopPosList.set(i, item + ";" + this.limitGetDefault(location));
                count++;
                break;
            }
        }

        if(count < 1) {
            playerShopPosList.add(shopPos + ";" + (this.limitGetDefault(location)));
        }

        this.playerConfig.getConfig().set("players." + playerName, playerShopPosList);
        this.playerConfig.saveConfig();
    }

    /**
     * 重置所有玩家对这个商店限制数量
     * @param location 箱子商店的坐标
     */
    public void limitResetAllPlayerTheDefault(Location location) {
        String shopPos = this.quickShopPlus.locationTrans(location);
        ConfigurationSection playerLimitShops = this.playerConfig.getConfig().getConfigurationSection("players");
        if(null != playerLimitShops) {
            List<String> playerNames = playerLimitShops.getKeys(true).stream().toList();
            for (int index = 0; index < playerNames.size(); index++) {
                List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playerNames.get(index));
                if(null != playerShopPosList) {
                    for (int i = 0; i < playerShopPosList.size(); i++) {
                        String item = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
                        if(item.equals(shopPos)) {
                            playerShopPosList.remove(i);
                            playerShopPosList.add(shopPos + ";" + this.quickShopPlus.getShoppingLimit().limitGetDefault(location));
                            this.playerConfig.getConfig().set("players." + playerNames.get(index), playerShopPosList);
                            break;
                        }
                    }
                }
            }
            this.playerConfig.saveConfig();
        }

    }

    /**
     * 重置所有商店到默认限制数量
     */
    public void limitResetAllTheDefault() {
        ConfigurationSection playerLimitShops = this.playerConfig.getConfig().getConfigurationSection("players");
        if(null != playerLimitShops) {
            List<String> playersName = playerLimitShops.getKeys(true).stream().toList();
            for(int index = 0; index < playersName.size(); index++) {
                List<String> playerShopPosList = (List<String>) this.playerConfig.getConfig().getList("players." + playersName.get(index));
                for (int i = 0; i < playerShopPosList.size(); i++) {
                    String shoppos = playerShopPosList.get(i).substring(0, playerShopPosList.get(i).lastIndexOf(";"));
                    if(this.quickShopPlus.getTimeReset().getShopIsAutoReset(shoppos)) {
                        String[] locationInfo = shoppos.split(";");
                        Location location = new Location(this.quickShopPlus.getServer().getWorld(locationInfo[3]),
                                Double.valueOf(locationInfo[0]),
                                Double.valueOf(locationInfo[1]),
                                Double.valueOf(locationInfo[2]));
                        this.limitResetPlayerTheDefault(playersName.get(index), location);
                    }
                }
            }
        }
    }

    /**
     * 删除指定玩家对指定商店限制数量
     * @param player 玩家
     * @param location 箱子商店的坐标
     */
    public void limitRemovePlayer(Player player, Location location) {
        this.limitSetPlayer(player, location, -255);
    }

}
