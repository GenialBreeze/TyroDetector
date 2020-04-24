package org.gbcraft.tyrodetector.help;

import com.djrapitops.plan.query.CommonQueries;
import com.djrapitops.plan.query.QueryService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PlanTimeHelper implements TimeHelper {

    /**
     * 查询对应玩家的游戏时间
     *
     * @param playerUUID 玩家UUID
     * @return 玩家游戏时间
     */
    @Override
    public Long getPlayHours(UUID playerUUID) {
        Long hours = 0L;

        CommonQueries queries = QueryService.getInstance().getCommonQueries();
        Set<UUID> servers = queries.fetchServerUUIDs();
        Long playtime = 0L;

        for (UUID uuid : servers) {
            playtime += queries.fetchPlaytime(playerUUID, uuid, 0, Long.MAX_VALUE);
        }

        hours = playtime / 3600000L;

        return hours;
    }

    /**
     * 查询对应玩家的游戏时间
     *
     * @param name 玩家名称
     * @return 玩家游戏时间
     */
    @Override
    public Long getPlayHours(String name) {
        Long hours = null;

        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            CommonQueries queries = QueryService.getInstance().getCommonQueries();
            Set<UUID> servers = queries.fetchServerUUIDs();
            UUID playerUUID = null;


            playerUUID = player.getUniqueId();
            Long playtime = 0L;

            for (UUID uuid : servers) {
                playtime += queries.fetchPlaytime(playerUUID, uuid, 0, Long.MAX_VALUE);
            }

            hours = playtime / 3600000L;
        }

        return hours;
    }
}
