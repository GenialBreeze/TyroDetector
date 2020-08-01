package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.ChatMessageHelper;
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
            UUID leader = TeamHelper.popTempLeader(member.getUniqueId());
            TeamHelper.cancelOutTimeTask(member.getUniqueId());
            if (null != leader) {
                Player lead = Bukkit.getPlayer(leader);
                if (null != lead) {
                    plugin.getPlayersConfig().addPlayers(leader, member.getUniqueId());
                    String successMsg = "&2绑定成功: " + lead.getName() + "与" + sender.getName();

                    lead.sendMessage(ChatMessageHelper.getMsg(successMsg));
                    member.sendMessage(ChatMessageHelper.getMsg(successMsg));
                }
            }
            else {
                String wrongMsg = "&c你没有待回复的邀请";
                sender.sendMessage(ChatMessageHelper.getMsg(wrongMsg));
            }
        }
    }
}
