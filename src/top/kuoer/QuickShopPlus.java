package top.kuoer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.localization.text.TextManager;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.api.shop.ShopType;
import org.maxgamer.quickshop.localization.text.LanguageFilesManager;
import org.maxgamer.quickshop.localization.text.SimpleTextManager;
import org.maxgamer.quickshop.shade.com.dumptruckman.bukkit.configuration.json.JsonConfiguration;
import org.maxgamer.quickshop.shop.ShopLoader;
import top.kuoer.command.CommandManager;
import top.kuoer.listener.PlayerListener;

import java.lang.reflect.Field;
import java.util.*;

public class QuickShopPlus extends JavaPlugin implements Listener {

    private QuickShop quickShop;
    // 如果玩家存在该列表则他正在编辑箱子商店
    public Map<Player, Location> playerChatModifyLimitMap = new HashMap<>();
    private LimitUtils shoppingLimit;
    private LimitUtils buyingLimit;
    private TimeReset timeReset;

    public ShopPlusConfig shoppingDefaultConfig;
    public ShopPlusConfig shoppingPlayerConfig;
    public ShopPlusConfig buyingDefaultConfig;
    public ShopPlusConfig buyingPlayerConfig;
    public ShopPlusConfig timeResetConfig;

    public LimiterTimer limiterTimer;


    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().isPluginEnabled("QuickShop")) {
            Bukkit.getConsoleSender().sendMessage("[QuickShopPlus] §a开始加载...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    init();
                    this.cancel();
                }
            }.runTaskLater(this,20 * 3);
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[QuickShopPlus] 需要QuickShop作为前置才能正常工作！！！");
        }
    }

    @Override
    public void onDisable() {
        this.limiterTimer.cancel();
    }

    public void init() {
        this.quickShop = (QuickShop) Bukkit.getPluginManager().getPlugin("QuickShop");

        Class<? extends QuickShop> quickShopClass = this.quickShop.getClass();
        try {
            SimpleShopManagerPlus simpleShopManagerPlus = new SimpleShopManagerPlus(this.quickShop, this);

            // 替换原有的shopManager
            Field shopManagerField = quickShopClass.getDeclaredField("shopManager");
            shopManagerField.setAccessible(true);
            shopManagerField.set(this.quickShop, simpleShopManagerPlus);


            // 替换语言
            SimpleTextManager textManager = (SimpleTextManager) this.quickShop.text();
            Field languageFilesManagerField = SimpleTextManager.class.getDeclaredField("languageFilesManager");
            languageFilesManagerField.setAccessible(true);
            LanguageFilesManager languageFilesManager = (LanguageFilesManager) languageFilesManagerField.get(textManager);
            Field locale2ContentMappingField = LanguageFilesManager.class.getDeclaredField("locale2ContentMapping");
            locale2ContentMappingField.setAccessible(true);
            Map<String, Map<String, JsonConfiguration>> maps = (Map<String, Map<String, JsonConfiguration>>) locale2ContentMappingField.get(languageFilesManager);
            JsonConfiguration jsonConfiguration = maps.get("/master/crowdin/lang/%locale%/messages.json").get("zh_cn");
            jsonConfiguration.set("how-many-buy", "&a聊天栏中输入想 &b购买 &a的物品数量。 输入 &b{1}&a 来购买全部物品。");
            jsonConfiguration.set("how-many-sell", "&a聊天栏中输入想 &d出售 &a的物品数量。 输入 &b{1}&a 来出售全部物品。");


            // 重新加载所有的商店
            Bukkit.getConsoleSender().sendMessage("[QuickShopPlus] §6正在重新加载QuickShop...");
            Field shopLoaderField = quickShopClass.getDeclaredField("shopLoader");
            shopLoaderField.setAccessible(true);
            ShopLoader shopLoader = (ShopLoader) shopLoaderField.get(this.quickShop);
            shopLoader.loadShops();

            Bukkit.getConsoleSender().sendMessage("[QuickShopPlus] §aQuickShop重新加载成功.");

            this.saveDefaultConfig();
            this.shoppingDefaultConfig = new ShopPlusConfig(this, "shoppingDefault.yml");
            this.shoppingDefaultConfig.saveDefaultConfig();
            this.shoppingPlayerConfig = new ShopPlusConfig(this, "shoppingPlayer.yml");
            this.shoppingPlayerConfig.saveDefaultConfig();
            this.buyingDefaultConfig = new ShopPlusConfig(this, "buyingDefault.yml");
            this.buyingDefaultConfig.saveDefaultConfig();
            this.buyingPlayerConfig = new ShopPlusConfig(this, "buyingPlayer.yml");
            this.buyingPlayerConfig.saveDefaultConfig();
            this.timeResetConfig = new ShopPlusConfig(this, "timeReset.yml");
            this.timeResetConfig.saveDefaultConfig();

            Bukkit.getPluginManager().registerEvents(new PlayerListener(this, this.quickShop), this);
            this.getCommand("qsp").setExecutor(new CommandManager(this));

            this.shoppingLimit = new LimitUtils(this, this.quickShop, this.shoppingDefaultConfig, this.shoppingPlayerConfig);
            this.buyingLimit = new LimitUtils(this, this.quickShop, this.buyingDefaultConfig, this.buyingPlayerConfig);
            this.timeReset = new TimeReset(this);

            // 创建限制定时器
            this.limiterTimer = new LimiterTimer(this);
            this.limiterTimer.runTaskTimer(this,0,5 * 20L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public LimitUtils getShoppingLimit() {
        return this.shoppingLimit;
    }

    public LimitUtils getBuyingLimit() {
        return this.buyingLimit;
    }

    public TimeReset getTimeReset() {
        return this.timeReset;
    }


    /**
     * 通过木牌朝向获取箱子商店
     */
    public void OakWallsignBlockFaceByShop(Location blockLocation, BlockFace blockFace) {
        if(blockFace == BlockFace.NORTH) {
            blockLocation.setZ(blockLocation.getZ() + 1);
        } else if(blockFace == BlockFace.SOUTH) {
            blockLocation.setZ(blockLocation.getZ() - 1);
        }else if(blockFace == BlockFace.WEST) {
            blockLocation.setX(blockLocation.getX() + 1);
        } else if(blockFace == BlockFace.EAST) {
            blockLocation.setX(blockLocation.getX() - 1);
        }
    }

    /**
     * 获得玩家指针指向的方块是否为商店
     * @param sender 发送命令的人
     * @return 箱子商店
     */
    public Shop getPointerShop(CommandSender sender, boolean isTips) {
        BlockIterator bIt = new BlockIterator((LivingEntity)sender, 10);

        int count = 0;
        while (bIt.hasNext()) {
            Block b = bIt.next();
            Shop shop = this.quickShop.getShopManager().getShop(b.getLocation());
            if(null == shop) {
                continue;
            }

            if(this.isShopExist(shop.getLocation())) {
                return shop;
            } else {
                if(isTips) {
                    sender.sendMessage("§c这个商店没有设置限制");
                }
            }
            count++;
            break;
        }

        if(count < 1) {
            if(isTips) {
                sender.sendMessage("§c请对着箱子商店进行修改");
            }
        }
        return null;
    }

    /**
     * 判断该商店是否存在限制
     * @param location 箱子商店坐标
     * @return boolean
     */
    public boolean isShopExist(Location location) {
        String shopPos = this.locationTrans(location);

        List<String> shopposList;
        Shop targetShop = this.quickShop.getShopManager().getShop(location);
        if(targetShop.getShopType() == ShopType.SELLING) {
            shopposList = (List<String>) this.shoppingDefaultConfig.getConfig().getList("shoppos");
        } else {
            shopposList = (List<String>) this.buyingDefaultConfig.getConfig().getList("shoppos");
        }
        boolean isExist = false;
        if(null != shopposList) {
            for (String item : shopposList) {
                item = item.substring(0, item.lastIndexOf(";"));
                if(item.equals(shopPos)) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }

    /**
     * 将Location转换成文本
     * @param location 箱子商店坐标
     * @return 转换后的文本
     */
    public String locationTrans(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String worldName = location.getWorld().getName();

        return x + ";" + y + ";" + z + ";" + worldName;
    }

}
