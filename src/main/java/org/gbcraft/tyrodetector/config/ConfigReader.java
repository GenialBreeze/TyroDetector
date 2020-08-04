package org.gbcraft.tyrodetector.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.bean.VHRule;

import java.io.File;
import java.util.*;

/**
 * 配置文件读取器
 */
public class ConfigReader {

    /**
     * 从默认配置文件中读取数据
     *
     * @param key 需要读取数据的键
     * @return 需要读取数据的值
     */
    public static String getParam(String key) {
        TyroDetector instance = TyroDetector.getPlugin();
        String res = null;
        if (instance != null) {
            res = instance.getConfig().getString(key);
        }

        return res;
    }

    /**
     * 从邮件配置文件中读取数据
     *
     * @param key 需要读取数据的键
     * @return 需要读取数据的值
     */
    public static String getEmailParam(String key) {
        TyroDetector instance = TyroDetector.getPlugin();
        String res = null;
        if (instance != null) {
            File file = new File(instance.getDataFolder(), "email.yml");
            Configuration emailConfig = YamlConfiguration.loadConfiguration(file);
            res = emailConfig.getString(key);
        }

        return res;
    }

    /**
     * 从默认配置文件中读取Map，需要提供一个根，若存在
     * 则以根下的键值对为基础生成Map实例
     *
     * @param root Map的根
     * @return 实例化的Map
     */
    public static Map<String, Integer> getParamMap(String root) {
        Map<String, Integer> res = new HashMap<>();
        TyroDetector instance = TyroDetector.getPlugin();
        FileConfiguration config = instance.getConfig();
        ConfigurationSection cs = config.getConfigurationSection(root);

        if (cs != null) {
            Set<String> keySet = cs.getKeys(false);
            for (String key : keySet) {
                res.put(key.toUpperCase(), cs.getInt(key));
            }
        }

        return res;
    }

    /**
     * 从默认配置文件中读取开关Map，需要提供一个根，若存在
     * 则以根下的键值对为基础生成Map实例
     *
     * @param root Map的根
     * @return 实例化的Map
     */
    public static Map<String, Boolean> getParamSwitchMap(String root) {
        Map<String, Boolean> res = new HashMap<>();
        TyroDetector instance = TyroDetector.getPlugin();
        FileConfiguration config = instance.getConfig();
        ConfigurationSection cs = config.getConfigurationSection(root);

        if (cs != null) {
            Set<String> keySet = cs.getKeys(false);
            for (String key : keySet) {
                res.put(key.toUpperCase(), cs.getBoolean(key));
            }
        }

        return res;
    }

    /**
     * 从默认配置文件中读取同时定义阈值与高度的规则Map, 需要提供一个根，若存在
     * 则以根下的键值对为基础生成Map实例
     *
     * @param root Map的根
     * @return 实例化的Map
     */
    public static Map<String, VHRule> getVHMap(String root) {
        Map<String, VHRule> res = new HashMap<>();
        TyroDetector instance = TyroDetector.getPlugin();
        FileConfiguration config = instance.getConfig();
        ConfigurationSection cs = config.getConfigurationSection(root);

        if (cs != null) {
            Set<String> keySet = cs.getKeys(false);
            for (String key : keySet) {
                String limit = key + ".limit";
                String height = key + ".height";
                res.put(key.toUpperCase(), new VHRule(cs.getInt(limit), cs.getInt(height)));
            }
        }

        return res;
    }

    /**
     * 从Email配置文件中获取一个列表
     *
     * @param key 列表名称
     * @return 列表实例
     */
    public static List<String> getEmailList(String key) {
        List<String> res = null;
        TyroDetector instance = TyroDetector.getPlugin();
        File file = new File(instance.getDataFolder(), "email.yml");
        Configuration config = YamlConfiguration.loadConfiguration(file);
        res = config.getStringList(key);

        return res;
    }

    /**
     * 从白名单配置文件中获取白名单列表
     *
     * @return 白名单实例
     */
    public static List<String> getWhiteList() {
        List<String> res = null;
        TyroDetector plugin = TyroDetector.getPlugin();
        File file = new File(plugin.getDataFolder(), "whitelist.yml");
        Configuration config = YamlConfiguration.loadConfiguration(file);
        res = config.getStringList("whitelist");

        return res;
    }

    /**
     * 从组队配置文件中获取组队列表
     *
     * @return 组队列表实例
     */
    public static Map<UUID, List<UUID>> getPlayers() {
        Map<UUID, List<UUID>> res = new HashMap<>();
        File file = new File(TyroDetector.getPlugin().getDataFolder(), "players.yml");
        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getDefaultSection();
        if (null != section) {
            Set<String> keys = section.getKeys(false);
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    res.put(UUID.fromString(key), getUUIDList(section.getStringList(key)));
                }
            }
        }
        return res;
    }

    private static List<UUID> getUUIDList(List<String> list) {
        List<UUID> res = new ArrayList<>();
        for (String s : list) {
            res.add(UUID.fromString(s));
        }
        return res;
    }


}
