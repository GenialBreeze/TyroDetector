package org.gbcraft.tyrodetector.help;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gbcraft.tyrodetector.TyroDetector;

import java.util.*;

/**
 * 监测玩家管理器
 */
public class TyroPlayersManager {
    private static Map<UUID,Player> tyroPlayers;

    /**
     * 初始化监测玩家列表(首次调用)并返回监测玩家Map
     * @return 监测玩家列表
     */
    public static Map<UUID, Player> getTyroPlayers(){
        if(tyroPlayers == null){
            TyroDetector plugin = TyroDetector.getPlugin();

            Map<UUID, Player> res = new HashMap<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!plugin.getWhiteListConfig().isContain(onlinePlayer.getName()) && TimeHelperManager.getPlayHours(onlinePlayer.getUniqueId()) <= plugin.getDetectorConfig().getTyroHours()) {
                    res.put(onlinePlayer.getUniqueId(), onlinePlayer);
                }
            }
            tyroPlayers = res;
        }
        return tyroPlayers;
    }

    /**
     * 尝试性的将一个玩家加入监测列表，仅在将某玩家手动移除白名单时使用
     * 若该玩家在线且游戏时间仍处于需要监测范围内才会真正将其加入监测列表
     * 注意：如果玩家不在线且仍需要监测{@link org.gbcraft.tyrodetector.listener.PlayerTyroListener#onPlayerJoin(PlayerJoinEvent)}
     * @param username 玩家名称
     */
    public static void tryAppend(String username){
        TyroDetector plugin = TyroDetector.getPlugin();
        Player player = Bukkit.getPlayer(username);
        if(null != player && TimeHelperManager.getPlayHours(player.getUniqueId()) <= plugin.getDetectorConfig().getTyroHours()){
            tyroPlayers.put(player.getUniqueId(), player);
        }
    }

    /**
     * 尝试性的将一个玩家移除出监测列表，仅在将某玩家手动加入白名单时使用
     * 若该玩家在线则将其移出监测玩家列表
     * 注意：若不在线{@link org.gbcraft.tyrodetector.listener.PlayerTyroListener#onPlayerQuit(PlayerQuitEvent)}
     * @param username 玩家名称
     */
    public static void tryRemove(String username) {
        TyroDetector plugin = TyroDetector.getPlugin();
        Player player = Bukkit.getPlayer(username);
        if(null != player){
            tyroPlayers.remove(player.getUniqueId());
        }
    }

}
