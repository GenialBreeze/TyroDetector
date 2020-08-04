package org.gbcraft.tyrodetector.prediction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PredictorManager {
    private static TyroDetector plugin;

    public static void init(){
        plugin = TyroDetector.getPlugin();
    }

    public static void tntPredict(HumanEntity player, Block block, BlockPlaceEvent event){
        PredictContainer pc = PredictContainer.getPredictContainer();
        Predictor predictor = new TntPredictor(block.getLocation());
        PredictedLevel level = predictor.predictDamage();

        if (level == PredictedLevel.SERVE) {
            event.setCancelled(true);
        }

        pc.putPredictor(player, level, predictor);

        if (TyroDetector.getPlugin().getDetectorConfig().getTntDupePredicate()) {
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

    public static void fluidPredict(HumanEntity player, Material bucket, PlayerBucketEmptyEvent event){
        Predictor predictor = new FluidPredictor(event.getBlockClicked().getLocation().clone().add(event.getBlockFace().getDirection()), bucket == Material.LAVA_BUCKET ? Material.LAVA : Material.WATER);
        PredictedLevel level = predictor.predictDamage();
        if (level == PredictedLevel.SERVE) {
            event.setCancelled(true);
        }
        PredictContainer.getPredictContainer().putPredictor(player, level, predictor);

    }

    public static void firePredict(HumanEntity player, Location location){
        Predictor predictor = new FirePredictor(location);
        PredictedLevel level = predictor.predictDamage();
        if (level == PredictedLevel.SERVE) {
            location.getBlock().setType(Material.AIR);
        }
        PredictContainer.getPredictContainer().putPredictor(player, level, predictor);
    }

    private static boolean findNearbyCoral(Location location) {
        for (BlockFace blockFace : BlockFace.values()) {
            Location newLocation = location.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
            String blockName = newLocation.getBlock().getType().name();
            if (blockName.contains("CORAL_FAN") || blockName.contains("CORAL_WALL_FAN")) {
                return true;
            }
        }

        return false;
    }

    private static boolean findNearbyBlock(Location location, Material targetMaterial) {
        for (BlockFace blockFace : BlockFace.values()) {
            Location newLocation = location.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
            if (newLocation.getBlock().getType() == targetMaterial) {
                return true;
            }
        }

        return false;
    }
}
