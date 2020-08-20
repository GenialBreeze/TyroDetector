package org.gbcraft.tyrodetector.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.PredictorManager;

public class PlayerInteractListener extends TDListener implements Listener {

    public PlayerInteractListener(TyroDetector plugin) {
        super(plugin);
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
                    }
                }, 5L);
            }
        }
    }
}
