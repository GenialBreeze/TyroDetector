package org.gbcraft.tyrodetector.config;

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

    public String getLeader(UUID member){
        String res = "无";
        UUID u = memToLeader.get(member);
        if(null != u){
            res = u.toString();
        }
        return res;
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
