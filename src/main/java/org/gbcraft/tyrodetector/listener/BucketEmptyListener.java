package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BucketEmptyListener extends ContainerListener<Material, Integer> implements Listener {

    public BucketEmptyListener(TyroDetector plugin) {
        super(plugin);
        // 周期性松弛缓存数据
        Bukkit.getScheduler().runTaskTimer(plugin, this::releaseAll, plugin.getDetectorConfig().getLiquidCycle() * 1200L, plugin.getDetectorConfig().getLiquidCycle() * 1200L);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getTyroPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        Material bucket = event.getBucket();
        Integer limit = plugin.getDetectorConfig().getLiquidMap().get(bucket.toString());

        if (null != limit) {
            plugin.logToFile("[DEBUG]发现需要监测的流体桶被放置 - " + event.getPlayer().getName());

            joinContainers(player, bucket, limit);
        }
    }

    @Override
    protected final void joinContainers(HumanEntity player, Material bucket, Integer limit) {
        Map<Material, Integer> playerBuckets = containers.computeIfAbsent(player, k -> new HashMap<>());
        playerBuckets.merge(bucket, 1, Integer::sum);
        if (playerBuckets.get(bucket) >= limit) {
            plugin.logToFile("[DEBUG]流体桶被使用次数达到上限,邮件准备");
            plugin.logToFile("[DEBUG]目标: " + player.getName() + " 流体桶类型: " + bucket.name());
            String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
            String content = player.getWorld().getName() +
                    " 放置 " + bucket.name() +
                    " x" + playerBuckets.get(bucket) +
                    " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                    " " + loc;
            EmailManager.getManager().append(player, new EmailInfo(content));
        }
    }
}
