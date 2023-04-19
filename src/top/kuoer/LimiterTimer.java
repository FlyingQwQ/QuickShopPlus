package top.kuoer;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Calendar;
import java.util.List;

public class LimiterTimer extends BukkitRunnable {

    private QuickShopPlus quickShopPlus;
    private int runTime = 0;

    public LimiterTimer(QuickShopPlus quickShopPlus) {
        this.quickShopPlus = quickShopPlus;
        this.updateRunTime();
    }

    // 更新任务执行时间
    public void updateRunTime() {
        this.runTime = this.quickShopPlus.getConfig().getInt("resetTime");
    }

    @Override
    public void run() {
        if(!this.quickShopPlus.getConfig().getBoolean("resetTimeEanble")) {
            return;
        }

        Calendar c = Calendar.getInstance();
        boolean isExecute = this.quickShopPlus.getConfig().getBoolean("isExecute");
        if(c.get(Calendar.HOUR_OF_DAY) == this.runTime) {
            if(!isExecute) {
                this.quickShopPlus.getConfig().set("isExecute", true);
                this.quickShopPlus.saveConfig();

                this.quickShopPlus.getShoppingLimit().limitResetAllTheDefault();
                this.quickShopPlus.getBuyingLimit().limitResetAllTheDefault();
            }
        } else {
            if(isExecute) {
                this.quickShopPlus.getConfig().set("isExecute", false);
                this.quickShopPlus.saveConfig();
            }
        }
    }

}
