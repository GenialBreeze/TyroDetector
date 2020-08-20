package org.gbcraft.tyrodetector.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.PredictorManager;

public class BucketEmptyListener extends TDListener implements Listener {

    public BucketEmptyListener(TyroDetector plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getTyroPlayers().containsKey(player.getUniqueId())) {
            return;
        }


        Material bucket = event.getBucket();
        // 流体桶风险预测
        PredictorManager.fluidPredict(player, bucket, event);
    }
}
