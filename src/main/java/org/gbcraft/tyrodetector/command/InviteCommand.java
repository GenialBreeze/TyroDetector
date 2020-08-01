package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.ChatMessageHelper;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;
import org.gbcraft.tyrodetector.help.TeamHelper;

import java.util.UUID;

public class InviteCommand extends TDCommand {
    public InviteCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        //获取sender uuid然后根据args(要绑定玩家的名称)获取要绑定玩家的uuid然后
        //把数据写入PlayersConfig里. 这些数据在邮件发送时作为额外数据附送
        String helpMsg0 = "/tyro invite <member>";
        String helpMsg1 = "/tyro invite <leader> <member>";
        if (args.length < 2) {
            if (sender.hasPermission("tyro.bind")) {
                sender.sendMessage(helpMsg0);
                sender.sendMessage(helpMsg1);
            }
            else {
                sender.sendMessage(helpMsg0);
            }
            return;
        }

        //有权限
        if (sender.hasPermission("tyro.bind")) {
            //管理员
            if (sender instanceof Player) {
                if (args.length >= 3) {
                    bind(args[1], args[2], true);
                }
                else {
                    bind(sender.getName(), args[1], false);
                }
            }
            //控制台
            else {
                if (args.length >= 3) {
                    bind(args[1], args[2], true);
                }
                else {
                    sender.sendMessage(helpMsg1);
                }
            }
        }
        //无权限
        else {
            //普通
            if (sender instanceof Player) {
                bind(sender.getName(), args[1], false);
            }
        }
    }

    private void bind(String leader, String member, Boolean enforce) {
        if (leader.equalsIgnoreCase(member)) {
            String lonelyMsg = "&c禁止自娱自乐!";
            sender.sendMessage(ChatMessageHelper.getMsg(lonelyMsg));
            return;
        }
        UUID lUUID = NameUUIDHelper.getUUID(leader);
        UUID mUUID = NameUUIDHelper.getUUID(member);

        if (null == lUUID || null == mUUID) {
            String playerNotFoundMsg = "&c队长/成员未找到, 请稍后再试";
            sender.sendMessage(ChatMessageHelper.getMsg(playerNotFoundMsg));
        }
        else {
            OfflinePlayer l = NameUUIDHelper.getOfflinePlayer(lUUID);
            OfflinePlayer m = NameUUIDHelper.getOfflinePlayer(mUUID);

            if (plugin.getPlayersConfig().isPartner(l.getUniqueId(), m.getUniqueId())) {
                String usurpMsg = "&c你们已经在同一队伍中了";
                sender.sendMessage(ChatMessageHelper.getMsg(usurpMsg));
            }
            else {
                // 不同队
                if (plugin.getPlayersConfig().hasLeader(m.getUniqueId())) {
                    String hadPartyMsg = "&c该玩家已有队伍";
                    sender.sendMessage(ChatMessageHelper.getMsg(hadPartyMsg));
                }
                else {
                    // 邀请人是否是队员, 如果是则将被邀请人转移到该队队长名下
                    if (plugin.getPlayersConfig().hasLeader(l.getUniqueId())) {
                        l = NameUUIDHelper.getOfflinePlayer(plugin.getPlayersConfig().getLeaderUUID(l.getUniqueId()));
                    }

                    if (enforce) {
                        // 管理员权限强制绑定
                        plugin.getPlayersConfig().addPlayers(l.getUniqueId(), m.getUniqueId());
                    }
                    else {
                        UUID tempLeader = TeamHelper.getTempLeader(m.getUniqueId());
                        if (null != tempLeader) {
                            String sameInviteMsg = "&c已经发起过同样的邀请了";
                            String hasInviteMsg = "&c对方有正在处理的邀请";
                            if (l.getUniqueId().equals(tempLeader)) {
                                sender.sendMessage(ChatMessageHelper.getMsg(sameInviteMsg));
                            }
                            else {
                                sender.sendMessage(ChatMessageHelper.getMsg(hasInviteMsg));
                            }


                        }
                        else {
                            if (null != m.getPlayer()) {
                                Player onlineMember = m.getPlayer();

                                TeamHelper.setRequests(onlineMember, l, (Player) sender);
                            }
                            else {
                                sender.sendMessage(ChatMessageHelper.getMsg("&4玩家不在线"));
                            }
                        }
                    }
                }


            }

        }

    }
}
