package org.gbcraft.tyrodetector.prediction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.util.LocationList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirePredictor implements Predictor {
    private final Location checkLocation;

    public FirePredictor(Location checkLocation) {
        this.checkLocation = checkLocation;
    }

    @Override
    public PredictedLevel predictDamage() {
        int level = predictDamageLevel();
        PredictedLevel predictedLevel = PredictedLevel.checkDamageProbability(level);
        TyroDetector.getPlugin().logToFile("[DEBUG]: 预测火焰危害等级：" + level + "，判定程度：" + predictedLevel);
        return predictedLevel;
    }

    @Override
    public int predictDamageLevel() {
        return checkBlock(checkLocation, new LocationList());
    }

    private int checkBlock(Location checkLocation, List<Location> dejaVu) {
        // 重复测算，不作记录
        if (dejaVu.contains(checkLocation)) {
            return 0;
        }
        dejaVu.add(checkLocation);

        int level = 0;
        // 可能会点燃tnt
        if (checkLocation.getBlock().getType() == Material.TNT) {
            level += new TntPredictor(checkLocation).predictDamageLevel();
        } else if (checkLocation.getBlock().getType().isFlammable()) {
            level = 2;
        }

        for (Location surrounding : getLocationSurrounding(checkLocation)) {
            try {
                level += checkBlock(surrounding, dejaVu);
            } catch (StackOverflowError error) {
                // 放火烧山？
                level = 114514;
            }
        }

        return level;
    }

    private List<Location> getLocationSurrounding(Location origin) {
        List<Location> surroundings = new ArrayList<>();
        for (BlockFace bf : BlockFace.values()) {
            Location loc = origin.clone().add(bf.getModX(), bf.getModY(), bf.getModZ());
            if (loc.getBlock().getType().isFlammable()) { // 仅处理会被点燃的方块
                surroundings.add(loc);
            }
        }
        return surroundings;
    }

    @Override
    public String toEmailContent(HumanEntity player, PredictedLevel cache) {
        String loc = "(X:" + checkLocation.getBlockX() + ",Z:" + checkLocation.getBlockZ() + ",Y:" + checkLocation.getBlockY() + ")";
        return player.getWorld().getName() +
                " 生火于 " +
                " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                " " + loc +
                " 严重性预测 " + cache;
    }
}
