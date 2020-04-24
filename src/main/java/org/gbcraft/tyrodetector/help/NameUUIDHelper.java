package org.gbcraft.tyrodetector.help;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 辅助工具类，提供服务器现存的玩家UUID与Name转换
 */
public class NameUUIDHelper {
    private final static Map<UUID, String> toNameMap = new HashMap<>();
    private final static Map<String, UUID> toUUIDMap = new HashMap<>();

    /**
     * 在插件启动时初始化，读取已有玩家信息
     */
    public static void init(){
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            toNameMap.put(offlinePlayer.getUniqueId(), offlinePlayer.getName());
            toUUIDMap.put(offlinePlayer.getName(), offlinePlayer.getUniqueId());
        }
    }

    public static boolean isContain(Player player){
        return toNameMap.get(player.getUniqueId())!=null;
    }

    /**
     * 对于首次加入服务器的玩家，手动添加相应信息
     * @param player 首次进入服务器的玩家实例
     */
    public static void append(Player player){
        toNameMap.put(player.getUniqueId(), player.getName());
        toUUIDMap.put(player.getName(), player.getUniqueId());
    }

    public static String getName(UUID uuid){
        return toNameMap.get(uuid);
    }

    public static UUID getUUID(String name){
        return toUUIDMap.get(name);
    }

}
