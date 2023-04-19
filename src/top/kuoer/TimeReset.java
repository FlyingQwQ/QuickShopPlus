package top.kuoer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TimeReset {

    private QuickShopPlus quickShopPlus;

    public TimeReset(QuickShopPlus quickShopPlus) {
        this.quickShopPlus = quickShopPlus;
    }

    public void toggle(Player player, Location location) {
        String shopPos = this.quickShopPlus.locationTrans(location);
        List<String> shopposList = (List<String>) this.quickShopPlus.timeResetConfig.getConfig().getList("shoppos");
        if(null == shopposList) {
            shopposList = new ArrayList<>();
        }
        for(int i = 0; i < shopposList.size(); i++) {
            if(shopposList.get(i).equals(shopPos)) {
                shopposList.remove(i);
                this.quickShopPlus.timeResetConfig.getConfig().set("shoppos", shopposList);
                this.quickShopPlus.timeResetConfig.saveConfig();
                player.sendMessage("§a这个商店将在 §e" + this.quickShopPlus.getConfig().getInt("resetTime") + "点 §a自动重置。");
                return;
            }
        }

        shopposList.add(shopPos);
        this.quickShopPlus.timeResetConfig.getConfig().set("shoppos", shopposList);
        this.quickShopPlus.timeResetConfig.saveConfig();
        player.sendMessage("§a已经将这个商店的自动重置取消了。");
    }

    public boolean getShopIsAutoReset(String shopPos) {
        List<String> shopposList = (List<String>) this.quickShopPlus.timeResetConfig.getConfig().getList("shoppos");
        if(null != shopposList) {
            for(String s : shopposList) {
                if(s.equals(shopPos)) {
                    return false;
                }
            }
        }
        return true;
    }

}
