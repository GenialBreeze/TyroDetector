package org.gbcraft.tyrodetector.config;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.command.ConfigCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 配置文件写入器
 */
public class ConfigWriter {
    /**
     * 添加或修改默认配置文件中的单个键值对
     *
     * @param key   需要添加或修改的键
     * @param value 键对应的值
     * @return 是否顺利执行
     */
    public static boolean setParam(String key, String value) {
        TyroDetector instance = TyroDetector.getPlugin();
        boolean res = false;
        if (instance != null) {
            FileConfiguration config = instance.getConfig();
            config.set(key, value);
            instance.saveConfig();
            res = true;
        }

        return res;
    }

    /**
     * 更新白名单，在白名单修改{@link WhiteListConfig#append(String)}和删除{@link WhiteListConfig#remove(String)}时使用。选择整体更新而非逐条更新是为了防止逐条更新过程
     * 中的误操作和数据混乱。
     *
     * @param list 白名单实例
     */
    public static void setWhiteList(List<String> list) {
        TyroDetector plugin = TyroDetector.getPlugin();
        File file = new File(plugin.getDataFolder(), "whitelist.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("whitelist", list);
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPlayers(UUID root, List<UUID> list) {
        File file = new File(TyroDetector.getPlugin().getDataFolder(), "players.yml");
        FileConfiguration players = YamlConfiguration.loadConfiguration(file);
        players.set(root.toString(), list);
        try {
            players.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成额外的配置文件，该配置文件根据{@link ConfigCommand}生成
     * 需要根据情况修改后手动导入到默认配置文件中
     *
     * @param content 物品键(物品内部ID)值(物品监测阈值)对
     */
    public static void generateExpendConfig(Map<String, Integer> content) {
        TyroDetector plugin = TyroDetector.getPlugin();
        String fileName = "expend" + new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()) + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileConfiguration ioHelper = YamlConfiguration.loadConfiguration(file);
                content.forEach((k, v) -> {
                    ioHelper.set("list." + k, v);
                });
                ioHelper.save(file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
