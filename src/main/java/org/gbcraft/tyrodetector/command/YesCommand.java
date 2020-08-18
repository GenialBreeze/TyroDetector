package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.bean.Inviter;
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
            Inviter inviter = TeamHelper.popInviter(member.getUniqueId());
            TeamHelper.cancelOutTimeTask(member.getUniqueId());
            if (null != inviter) {
                plugin.getPlayersConfig().addPlayers(inviter.getLeader(), member.getUniqueId());
                String joinedTeam = "&2" + member.getName() + "加入了队伍!";
                String joinTeam = "&2邀请已接受, 组队成功!";

                Player leader = Bukkit.getPlayer(inviter.getLeader());
                Player inv = Bukkit.getPlayer(inviter.getInviter());

                if (null != leader && leader.isOnline()) {
                    leader.sendMessage(ChatMessageHelper.getMsg(joinedTeam));
                }
                if (null != inv && inv.isOnline()) {
                    inv.sendMessage(ChatMessageHelper.getMsg(joinedTeam));
                }
                if (member.isOnline()) {
                    member.sendMessage(ChatMessageHelper.getMsg(joinTeam));
                }
            }
            else {
                String wrongMsg = "&c你没有待回复的邀请";
                sender.sendMessage(ChatMessageHelper.getMsg(wrongMsg));
            }
        }
    }
}
