package org.gbcraft.tyrodetector.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;
import org.gbcraft.tyrodetector.help.TimeHelperManager;

public class PlayerTyroListener implements Listener {
    private final TyroDetector plugin;

    public PlayerTyroListener(TyroDetector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //首次加入？
        if (!NameUUIDHelper.isContain(player)) {
            NameUUIDHelper.append(player);
        }
        //若尚未监控
        if (!plugin.getTyroPlayers().containsKey(player.getUniqueId())) {
            //应处于监控状态
            if (!plugin.getWhiteListConfig().isContain(player.getName()) && TimeHelperManager.getPlayHours(player.getUniqueId()) <= plugin.getDetectorConfig().getTyroHours()) {
                plugin.getTyroPlayers().put(player.getUniqueId(), event.getPlayer());
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getTyroPlayers().remove(player.getUniqueId());
    }

}
