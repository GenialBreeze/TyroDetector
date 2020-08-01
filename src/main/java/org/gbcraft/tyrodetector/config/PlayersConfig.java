package org.gbcraft.tyrodetector.config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;

import java.util.*;

public class PlayersConfig {
    private final Map<UUID, List<UUID>> players;
    private final Map<UUID, UUID> memToLeader;

    public PlayersConfig() {
        this.players = ConfigReader.getPlayers();
        memToLeader = new HashMap<>();
        players.forEach((k, v) -> {
            v.forEach(m -> {
                memToLeader.put(m, k);
            });
        });
    }

    public boolean isPartner(UUID p1, UUID p2) {
        boolean res = true;
        UUID p1Leader = memToLeader.get(p1);
        UUID p2Leader = memToLeader.get(p2);
        if (null != p1Leader) {
            // A有队长
            if (null != p2Leader) {
                // B有队长, 队长不同则不同队伍
                if (!p1Leader.equals(p2Leader)) {
                    res = false;
                }
            }
            else {
                // A有队长B没有 若B不是A的队长两人不同队
                if (!p1Leader.equals(p2)) {
                    res = false;
                }
            }
        }
        else {
            // A没有队长
            if (null != p2Leader) {
                // B有队长 若A不为B的队长则不同队
                if (!p2Leader.equals(p1)) {
                    res = false;
                }
            }
            else {
                // B也没有队长
                res = false;
            }
        }

        return res;
    }

    public boolean hasLeader(UUID uuid){
        return memToLeader.get(uuid) != null;
    }

    public String getLeader(String member){
        Player m = Bukkit.getPlayer(member);
        if(null != m){
            return getLeader(m.getUniqueId());
        }
        return null;
    }

    public String getLeader(UUID member) {
        String res = null;
        UUID u = memToLeader.get(member);
        if (null != u) {
            res = NameUUIDHelper.getName(u);
        }
        else if (null != players.get(member)) {
            res = NameUUIDHelper.getName(member);
        }

        return res;
    }

    public UUID getLeaderUUID(UUID member){
        return memToLeader.get(member);
    }

    public String getPartyInfo(UUID player) {
        UUID leader = memToLeader.get(player);
        String res = null;
        if (null != leader) {
            // 自己是队员
            res = buildPartyInfo(leader);
        }
        else {
            // 自己是队长或特殊情况无队伍
            res = buildPartyInfo(player);
        }

        return res;
    }

    private String buildPartyInfo(UUID leader) {
        StringBuilder res = new StringBuilder();
        List<UUID> members = players.get(leader);
        if (null != members && !members.isEmpty()) {
            res.append("队长: ").append(NameUUIDHelper.getName(leader));
            res.append("\n 队员:");
            members.forEach(m -> {
                res.append(NameUUIDHelper.getName(m)).append(" ");
            });
        }
        else {
            res.append("无");
        }

        return res.toString();
    }

    public void addPlayers(UUID leader, UUID member) {
        List<UUID> list = players.get(leader);
        if (null != list) {
            list.add(member);
        }
        else {
            list = new ArrayList<>();
            list.add(member);
            players.put(leader, list);
        }

        ConfigWriter.setPlayers(leader, list);
    }
}
