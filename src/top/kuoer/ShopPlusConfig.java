package top.kuoer;

import org.apache.commons.io.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ShopPlusConfig {

    private QuickShopPlus quickShopPlus;
    private String fileName;
    private File configFile = null;
    private FileConfiguration newConfig = null;

    public ShopPlusConfig(QuickShopPlus quickShopPlus, String fileName) {
        this.quickShopPlus = quickShopPlus;
        this.fileName = fileName;

        this.configFile = new File(this.quickShopPlus.getDataFolder(), fileName);
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.quickShopPlus.saveResource(this.fileName, false);
        }
    }

    public FileConfiguration getConfig() {
        if (this.newConfig == null) {
            this.reloadConfig();
        }
        return this.newConfig;
    }

    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = this.quickShopPlus.getResource(this.fileName);
        if (defConfigStream != null) {
            this.newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException var2) {
            Bukkit.getLogger().log(Level.SEVERE, "无法将配置保存到 " + this.configFile, var2);
        }

    }

}
