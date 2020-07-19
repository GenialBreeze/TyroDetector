package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.gbcraft.tyrodetector.TyroDetector;

/**
 * 该命令提供GUI式的物品配置文件书写功能，将需要监测的物品在打开的GUI中
 * 放入指定阈值数，自动在配置文件夹下生成需要的配置
 * 注意：生成的配置文件需要手动导入来保证数据的正确性，另外也留出修改的空间
 */
public class ConfigCommand extends TDCommand {
    public ConfigCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender.hasPermission("tyro.config")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Inventory box = Bukkit.createInventory(null, 54, "TyroDetector Configuration Box");
                player.openInventory(box);
            }
        }

    }

}
