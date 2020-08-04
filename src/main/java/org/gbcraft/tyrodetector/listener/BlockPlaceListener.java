package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 方块放置监测器
 */
public class BlockPlaceListener extends ContainerListener<Block, Integer> implements Listener {
    public BlockPlaceListener(TyroDetector plugin) {
        super(plugin);
        // 周期性松弛缓存数据
        Bukkit.getScheduler().runTaskTimer(plugin, this::releaseAll, plugin.getDetectorConfig().getPlaceCycle() * 1200L, plugin.getDetectorConfig().getPlaceCycle() * 1200L);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // 判定不在监测范围的玩家
        if (!plugin.getTyroPlayers().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        HumanEntity player = event.getPlayer();
        Block block = event.getBlockPlaced();

        Integer limit = plugin.getDetectorConfig().getPlaceMap().get(block.getType().name());
        //如果是需要监测的方块
        if (null != limit) {
            plugin.logToFile("[DEBUG]发现需要监测的方块被放置 - " + player.getName());

            joinContainers(player, block, limit);
        }

        if (block.getType() == Material.TNT) {
            Location detectLocation = block.getLocation();
            boolean hasPiston = findNearbyBlock(detectLocation, Material.PISTON) || findNearbyBlock(detectLocation, Material.PISTON_HEAD) || findNearbyBlock(detectLocation, Material.STICKY_PISTON);
            boolean hasSlimeBlock = findNearbyBlock(detectLocation, Material.SLIME_BLOCK);
            String serverVer = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
            int minorVer = Integer.parseInt(serverVer.split("_")[1]);
            // 注意蜜蜂块
            if (minorVer >= 15) {
                hasSlimeBlock = hasSlimeBlock || findNearbyBlock(detectLocation, Material.HONEY_BLOCK);
            }

            boolean hasCoralFan;
            // CPU并不勤快 并提出了更优雅的写法
            hasCoralFan = findNearbyCoral(detectLocation);

            if (hasPiston || (hasSlimeBlock && hasCoralFan)) {
                plugin.logToFile("[DEBUG]疑似TNT复制,邮件准备");
                plugin.logToFile("[DEBUG]目标: " + player.getName() + " 方块类型: " + block.getType().name());
                String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
                String content = player.getWorld().getName() +
                        " 放置 " + block.getType().name() +
                        " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                        " " + loc + "，疑似构成TNT复制";
                EmailManager.getManager().append(player, new EmailInfo(content));
            }
        }
    }

    @Override
    protected final void joinContainers(HumanEntity player, Block block, Integer limit) {
        //获取玩家的物品放置表, 如果不存在则新建
        Map<Block, Integer> playerBlocks = containers.computeIfAbsent(player, k -> new HashMap<>());
        //将放置的方块自增1, 若不存在该方块对应的值则新增 <BlockName:1> 键值对
        playerBlocks.merge(block, 1, Integer::sum);
        //若该方块数目达到监测值, 发送邮件
        if (playerBlocks.get(block) >= limit) {
            plugin.logToFile("[DEBUG]方块被放置次数达到上限,邮件准备");
            plugin.logToFile("[DEBUG]目标: " + player.getName() + " 方块类型: " + block.getType().name());
            String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
            String content = player.getWorld().getName() +
                    " 放置 " + block.getType().name() +
                    " x" + playerBlocks.get(block) +
                    " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                    " " + loc;
            EmailManager.getManager().append(player, new EmailInfo(content));
        }
    }

    private boolean findNearbyCoral(Location location) {
        for (BlockFace blockFace : BlockFace.values()) {
            Location newLocation = location.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
            String blockName = newLocation.getBlock().getType().name();
            if (blockName.contains("CORAL_FAN") || blockName.contains("CORAL_WALL_FAN")) {
                return true;
            }
        }

        return false;
    }

    private boolean findNearbyBlock(Location location, Material targetMaterial) {
        for (BlockFace blockFace : BlockFace.values()) {
            Location newLocation = location.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
            if (newLocation.getBlock().getType() == targetMaterial) {
                return true;
            }
        }

        return false;
    }
}
