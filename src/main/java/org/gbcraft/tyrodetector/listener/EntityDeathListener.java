package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.config.LanguageConfig;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体死亡监测器
 */
public class EntityDeathListener extends ContainerListener<Entity, Integer> implements Listener {
    public EntityDeathListener(TyroDetector plugin) {
        super(plugin);
        // 周期性松弛缓存数据
        Bukkit.getScheduler().runTaskTimer(plugin, this::releaseAll, plugin.getDetectorConfig().getEntityCycle() * 1200L, plugin.getDetectorConfig().getEntityCycle() * 1200L);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity deathEntity = event.getEntity();
        HumanEntity player = event.getEntity().getKiller();
        if (null == player) {
            return;
        }

        if (!plugin.getTyroPlayers().containsKey(player.getUniqueId())) {
            return;
        }
        Integer limit = plugin.getDetectorConfig().getEntityMap().get(deathEntity.getType().name());
        if (null != limit) {
            plugin.logToFile("[DEBUG]发现监测实体死亡 - " + player.getName());

            joinContainers(player, deathEntity, limit);
        }


    }

    @Override
    protected final void joinContainers(HumanEntity player, Entity entity, Integer limit) {
        Map<Entity, Integer> playerEntities = containers.computeIfAbsent(player, k -> new HashMap<>());
        playerEntities.merge(entity, 1, Integer::sum);
        if (playerEntities.get(entity) >= limit) {
            plugin.logToFile("[DEBUG]实体死亡次数达到上限,邮件准备");
            plugin.logToFile("[DEBUG]目标: " + player.getName() + " 实体类型: " + LanguageConfig.getName(entity.getType()));
            String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
            String content = player.getWorld().getName() +
                    " 杀死 " + LanguageConfig.getName(entity.getType()) +
                    " - " + entity.getName() +
                    " x" + playerEntities.get(entity) +
                    " " + new SimpleDateFormat("HH:mm").format(new Date())
                    + " " + loc;
            EmailManager.getManager().append(player, new EmailInfo(content));
        }
    }

}
