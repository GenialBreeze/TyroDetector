package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.bean.VHRule;
import org.gbcraft.tyrodetector.config.DetectorConfig;
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
        VHRule rule = plugin.getDetectorConfig().getLiquidMap().get(bucket.toString());
        Integer height = rule.getHeight();

        boolean isFlame = false;
        if (bucket == Material.LAVA_BUCKET) {
            isFlame = isFlame(event);
        }

        if ((null != height && player.getLocation().getBlockY() >= height) || isFlame) {
            Integer limit = rule.getLimit();

            if (null != limit) {
                plugin.logToFile("[DEBUG]发现需要监测的流体桶被放置 - " + event.getPlayer().getName());

                joinContainers(player, bucket, limit);
            }
        }

    }

    private boolean isFlame(PlayerBucketEmptyEvent event) {
        boolean isFlame = false;

        Location location = event.getBlockClicked().getLocation().clone();
        double preY = location.getY();
        double preZ = location.getZ();

        int r = plugin.getDetectorConfig().getLavaBucketRange();
        for (int x = -r; r > 0 && x <= r && !isFlame; x++) {
            location.add(x, 0, 0);
            location.setY(preY);
            for (int y = -r; y <= r && !isFlame; y++) {
                location.add(0, y, 0);
                location.setZ(preZ);
                for (int z = -r; z <= r && !isFlame; z++) {
                    location.add(0, 0, z);
                    if (location.getBlock().getType().isFlammable()) {
                        isFlame = true;
                    }
                }
            }
        }

        return isFlame;
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
