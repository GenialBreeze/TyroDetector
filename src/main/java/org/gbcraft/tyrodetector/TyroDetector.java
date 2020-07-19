package org.gbcraft.tyrodetector;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.gbcraft.tyrodetector.command.TDCommandExecutor;
import org.gbcraft.tyrodetector.config.DetectorConfig;
import org.gbcraft.tyrodetector.config.EmailConfig;
import org.gbcraft.tyrodetector.config.PlayersConfig;
import org.gbcraft.tyrodetector.config.WhiteListConfig;
import org.gbcraft.tyrodetector.email.EmailManager;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;
import org.gbcraft.tyrodetector.help.PlanTimeHelper;
import org.gbcraft.tyrodetector.help.TimeHelperManager;
import org.gbcraft.tyrodetector.help.TyroPlayersManager;
import org.gbcraft.tyrodetector.listener.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public final class TyroDetector extends JavaPlugin {

    private static TyroDetector plugin;
    private DetectorConfig detectorConfig;
    private EmailConfig emailConfig;
    private PlayersConfig playersConfig;
    private WhiteListConfig whiteListConfig;
    private Map<UUID, Player> tyroPlayers;

    public Map<UUID, Player> getTyroPlayers() {
        return tyroPlayers;
    }

    @Override
    public void onEnable() {
        plugin = this;
        NameUUIDHelper.init();

        /*生成默认配置*/
        saveDefaultConfig();
        saveResource("email.yml", false);
        saveResource("whitelist.yml", false);

        /*实例化配置文件*/
        detectorConfig = new DetectorConfig();
        emailConfig = new EmailConfig();
        playersConfig = new PlayersConfig();
        whiteListConfig = new WhiteListConfig();

        /*注册玩家游戏时间获取器*/
        TimeHelperManager.registerTimeHelper(new PlanTimeHelper());

        /*获取所有监测玩家*/
        tyroPlayers = TyroPlayersManager.getTyroPlayers();

        logToFile("配置文件初始化完毕");
        logToFile("[DEBUG]规则配置文件信息:\n" + detectorConfig.toString());

        this.getLogger().info("TyroDetector启动中...");
        /*注册监听器*/
        Bukkit.getPluginManager().registerEvents(new PlayerTyroListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BucketEmptyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);

        Bukkit.getPluginManager().registerEvents(new ConfigBoxListener(this), this);

        /*注册命令执行器*/
        TDCommandExecutor executor = new TDCommandExecutor(plugin);
        PluginCommand tyro = Bukkit.getPluginCommand("tyro");
        if (null != tyro) {
            tyro.setExecutor(executor);
            tyro.setTabCompleter(executor);
        }

        cycleTaskInit();
        this.getLogger().info("TyroDetector初始化完成!");
    }

    @Override
    public void onDisable() {
        saveLog();
    }

    BukkitTask emailCycleTask;
    BukkitTask whiteCycleTask;

    private void cycleTaskInit() {
        releaseCycle(emailCycleTask);
        releaseCycle(whiteCycleTask);

        boolean cycle;
        /*启用邮件周期任务*/
        emailCycleTask = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
            logToFile("[DEBUG]邮件周期日志准备中");
            //周期邮件报告
            EmailManager.getManager().sendAll();
            logToFile("[DEBUG]邮件周期日志发送完毕");
            //松弛监测
            tyroPlayers.forEach((key, value) -> {
                if (TimeHelperManager.getPlayHours(key) > plugin.getDetectorConfig().getTyroHours()) {
                    plugin.getTyroPlayers().remove(key);
                }
            });
            logToFile("[DEBUG]监测信息松弛完毕");
        }, emailConfig.getTime() * 1200L, emailConfig.getTime() * 1200L);
        cycle = emailCycleTask.isCancelled();
        logToFile("[DEBUG]邮件周期日志是否被取消: " + cycle);

        /*启用白名单松弛周期任务*/
        whiteCycleTask = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
            whiteListConfig.releaseAll();
            logToFile("[DEBUG]白名单松弛完毕");
        }, detectorConfig.getWhiteCycle() * 1200L, detectorConfig.getWhiteCycle() * 1200L);

        cycle = whiteCycleTask.isCancelled();
        logToFile("[DEBUG]白名单周期是否被取消: " + cycle);
    }

    private void releaseCycle(BukkitTask task) {
        if (null != task && !task.isCancelled()) {
            task.cancel();
        }
    }

    /**
     * 将信息输出到日志文件中，日志位于/log文件夹下
     *
     * @param msg 需要输出的日志信息正文部分，直接提供需要输出的信息，无需考虑格式问题。
     */
    public void logToFile(String msg) {
        if (!detectorConfig.getDebug())
            return;

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File saveTo = new File(getDataFolder(), "log/log.txt");
        try {
            if (!saveTo.getParentFile().exists()) {
                saveTo.getParentFile().mkdirs();
            }
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            msg = new SimpleDateFormat("[HH:mm:ss]: ").format(new Date()) + msg;
            pw.println(msg);
            pw.flush();
            pw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 备份当前日志文件，只在插件完全重载或卸载时调用
     */
    private void saveLog() {
        if (!detectorConfig.getDebug())
            return;

        File log = new File(getDataFolder(), "log/log.txt");
        File saveLog = new File(getDataFolder(), "log/log-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
        if (log.exists()) {
            log.renameTo(saveLog);
        }
    }

    /*重新载入配置文件实例
    注意：白名单不应该支持热重载以保证数据的安全性和一致性*/
    public void rebuildConfigInstance() {
        saveDefaultConfig();
        reloadConfig();
        detectorConfig = new DetectorConfig();
        emailConfig = new EmailConfig();
        playersConfig = new PlayersConfig();
        cycleTaskInit();
    }

    public static TyroDetector getPlugin() {
        return plugin;
    }

    public DetectorConfig getDetectorConfig() {
        return detectorConfig;
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public PlayersConfig getPlayersConfig() {
        return playersConfig;
    }

    public WhiteListConfig getWhiteListConfig() {
        return whiteListConfig;
    }
}
