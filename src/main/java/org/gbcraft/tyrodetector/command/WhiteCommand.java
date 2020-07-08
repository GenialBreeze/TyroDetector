package org.gbcraft.tyrodetector.command;

import org.bukkit.command.CommandSender;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.TyroPlayersManager;

import java.util.Arrays;

/**
 * 白名单命令，提供白名单基本的增删查操作
 */
public class WhiteCommand extends TDCommand {

    public WhiteCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender.hasPermission("tyro.base")) {
            if (args.length < 2) {
                sender.sendMessage("/tyro white {add|remove|list} <playername>");
                return;
            }
            switch (args[1]) {
                case "add":
                    plugin.getWhiteListConfig().append(args[2]);
                    TyroPlayersManager.tryRemove(args[2]);
                    break;
                case "remove":
                    plugin.getWhiteListConfig().remove(args[2]);
                    TyroPlayersManager.tryAppend(args[2]);
                    break;
                case "list":
                    sender.sendMessage(Arrays.toString(plugin.getWhiteListConfig().list()));
                    StringBuilder builder = new StringBuilder();
                    plugin.getTyroPlayers().forEach((k, v) -> {
                        builder.append(v.getName());
                    });
                    sender.sendMessage("[DEBUG]这里是当前监测玩家" + builder.toString());
                    break;
                default:
                    break;
            }
        }
    }
}
