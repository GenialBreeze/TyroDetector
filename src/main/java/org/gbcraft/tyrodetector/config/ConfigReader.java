package org.gbcraft.tyrodetector.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gbcraft.tyrodetector.TyroDetector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件读取器
 */
public class ConfigReader {

    /**
     * 从默认配置文件中读取数据
     * @param key 需要读取数据的键
     * @return 需要读取数据的值
     */
    public static String getParam(String key){
        TyroDetector instance = TyroDetector.getPlugin();
        String res = null;
        if(instance != null){
            res = instance.getConfig().getString(key);
        }

        return res;
    }

    /**
     * 从邮件配置文件中读取数据
     * @param key 需要读取数据的键
     * @return 需要读取数据的值
     */
    public static String getEmailParam(String key){
        TyroDetector instance = TyroDetector.getPlugin();
        String res = null;
        if(instance != null){
            File file = new File(instance.getDataFolder(), "email.yml");
            Configuration emailConfig = YamlConfiguration.loadConfiguration(file);
            res = emailConfig.getString(key);
        }

        return res;
    }

    /**
     * 从默认配置文件中读取Map，需要提供一个根，若存在
     * 则以根下的键值对为基础生成Map实例
     * @param root Map的根
     * @return 实例化的Map
     */
    public static Map<String, Integer> getParamMap(String root){
        Map<String, Integer> res = new HashMap<>();
        TyroDetector instance = TyroDetector.getPlugin();
        FileConfiguration config = instance.getConfig();
        ConfigurationSection cs = config.getConfigurationSection(root);

        if (cs != null) {
            Set<String> keySet = cs.getKeys(false);
            for(String key : keySet){
                res.put(key.toUpperCase(), cs.getInt(key));
            }
        }

        return res;
    }

    /**
     * 从Email配置文件中获取一个列表
     * @param key 列表名称
     * @return 列表实例
     */
    public static List<String> getEmailList(String key){
        List<String> res = null;
        TyroDetector instance = TyroDetector.getPlugin();
        File file = new File(instance.getDataFolder(), "email.yml");
        Configuration config = YamlConfiguration.loadConfiguration(file);
        res = config.getStringList(key);

        return res;
    }

    /**
     * 从白名单配置文件中获取白名单列表
     * @return 白名单实例
     */
    public static List<String> getWhiteList(){
        List<String> res = null;
        TyroDetector plugin = TyroDetector.getPlugin();
        File file = new File(plugin.getDataFolder(), "whitelist.yml");
        Configuration config = YamlConfiguration.loadConfiguration(file);
        res = config.getStringList("whitelist");

        return res;
    }



}
