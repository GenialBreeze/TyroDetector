package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.LanguageHelper;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 方块破坏监测器
 */
public class BlockBreakListener extends ContainerListener<Block, Integer> implements Listener {
    public BlockBreakListener(TyroDetector plugin) {
        super(plugin);
        // 周期性松弛缓存数据
        Bukkit.getScheduler().runTaskTimer(plugin, this::releaseAll, plugin.getDetectorConfig().getBrokenCycle() * 1200L, plugin.getDetectorConfig().getBrokenCycle() * 1200L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // 判定不在监测范围的玩家
        if (!plugin.getTyroPlayers().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        HumanEntity player = event.getPlayer();
        Block block = event.getBlock();

        // 获取对应方块的阈值
        Integer limit = plugin.getDetectorConfig().getBrokenMap().get(block.getType().name());
        // 如果是需要监测的方块
        if (null != limit) {
            plugin.logToFile("[DEBUG]发现需要监测的方块被破坏 - " + player.getName());

            joinContainers(player, block, limit);
        }
    }

    @Override
    protected final void joinContainers(HumanEntity player, Block block, Integer limit) {
        //获取玩家的破坏物品表, 如果不存在则新建
        Map<Block, Integer> playerBlocks = containers.computeIfAbsent(player, k -> new HashMap<>());
        //将破坏的方块自增1, 若不存在该方块对应的值则新增 <BlockName:1> 键值对
        playerBlocks.merge(block, 1, Integer::sum);
        //若该方块数目达到监测值, 发送邮件
        if (playerBlocks.get(block) >= limit) {
            plugin.logToFile("[DEBUG]方块被破坏次数达到上限,邮件准备");
            plugin.logToFile("[DEBUG]目标: " + player.getName() + " 方块类型: " + LanguageHelper.getName(block.getType()));
            String loc = "(" + player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ() + ")";
            String content = player.getWorld().getName() +
                    " 破坏 " + LanguageHelper.getName(block.getType()) +
                    " x" + playerBlocks.get(block) +
                    " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                    " " + loc;
            EmailManager.getManager().append(player, new EmailInfo(content));
        }

    }
}
