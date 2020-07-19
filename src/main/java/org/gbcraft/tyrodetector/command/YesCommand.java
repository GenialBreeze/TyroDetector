package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.config.PlayersConfig;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;
import org.gbcraft.tyrodetector.help.TeamHelper;

import java.util.UUID;

public class YesCommand extends TDCommand {

    public YesCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender instanceof Player) {
            Player member = (Player) sender;
            UUID leader = TeamHelper.popLeader(member.getUniqueId());
            if (null != leader) {
                plugin.getPlayersConfig().addPlayers(leader, member.getUniqueId());
                Player lead = Bukkit.getPlayer(leader);
                if (null != lead) {
                    lead.sendMessage("绑定成功: " + lead.getName() + "与" + sender.getName());
                    sender.sendMessage("绑定成功: " + lead.getName() + "与" + sender.getName());
                }
            }
            else {
                sender.sendMessage("你没有待回复的邀请");
            }
        }
    }
}
