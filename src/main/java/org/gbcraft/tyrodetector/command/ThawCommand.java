package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.freeze.FreezeManager;
import org.gbcraft.tyrodetector.help.ChatMessageHelper;

// 解冻命令
public class ThawCommand extends TDCommand {
    public ThawCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender.hasPermission("tyro.thaw")) {
            if (args.length != 1) {
                sender.sendMessage(ChatMessageHelper.getMsg("&c用法：/tyro thaw <你要解冻的玩家>"));
                return;
            }
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(ChatMessageHelper.getMsg("&c你所指的玩家是谁，他/她不在服务器上"));
                return;
            }
            FreezeManager.getFreezeManager().thawPlayer(p);
            sender.sendMessage(ChatMessageHelper.getMsg("&a已解冻该玩家"));
        }
    }
}
