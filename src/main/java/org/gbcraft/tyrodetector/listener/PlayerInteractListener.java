package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;
import org.gbcraft.tyrodetector.prediction.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerInteractListener extends ContainerListener<Material, Integer> implements Listener {
    public PlayerInteractListener(TyroDetector plugin) {
        super(plugin);
        // 周期性松弛缓存数据
        Bukkit.getScheduler().runTaskTimer(plugin, this::releaseAll, plugin.getDetectorConfig().getFireCycle() * 1200L, plugin.getDetectorConfig().getFireCycle() * 1200L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getTyroPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (null != clickedBlock) {
                Location location = clickedBlock.getLocation().clone();
                location.add(event.getBlockFace().getModX(), event.getBlockFace().getModY(), event.getBlockFace().getModZ());

                if (clickedBlock.getType() == Material.TNT) {
                    // tnt预测
                    BlockPlaceEvent bpe = new BlockPlaceEvent(clickedBlock, clickedBlock.getState(), clickedBlock, player.getInventory().getItemInMainHand(), player, true, EquipmentSlot.HAND);
                    PredictorManager.tntPredict(player, clickedBlock, bpe);
                    if (bpe.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (location.getBlock().getType() == Material.FIRE) {
                        // 火势风险预测
                        PredictorManager.firePredict(player, location);

                        ItemStack item = event.getItem();
                        if (null != item) {
                            Integer limit = plugin.getDetectorConfig().getFireMap().get(item.getType().name());
                            if (null != limit) {
                                plugin.logToFile("[DEBUG]点燃火焰 - " + event.getPlayer().getName());

                                joinContainers(player, item.getType(), limit);
                            }
                        }
                    }
                }, 5L);
            }
        }
    }


    @Override
    protected final void joinContainers(HumanEntity player, Material item, Integer limit) {
        //获取玩家的物品放置表, 如果不存在则新建
        Map<Material, Integer> playerItems = containers.computeIfAbsent(player, k -> new HashMap<>());
        //将放置的方块自增1, 若不存在该方块对应的值则新增 <BlockName:1> 键值对
        playerItems.merge(item, 1, Integer::sum);
        //若该方块数目达到监测值, 发送邮件
        if (playerItems.get(item) >= limit) {
            plugin.logToFile("[DEBUG]火焰次数达到上限,邮件准备");
            plugin.logToFile("[DEBUG]目标: " + player.getName() + " 使用道具类型: " + item.name());
            String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
            String content = player.getWorld().getName() +
                    " 使用 " + item.name() +
                    " 点火x" + playerItems.get(item) +
                    " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                    " " + loc;
            EmailManager.getManager().append(player, new EmailInfo(content));
        }
    }
}
