package org.gbcraft.tyrodetector.prediction;

import cn.mcres.luckyfish.plugincommons.utils.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.util.LocationList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FluidPredictor extends Predictor {
    private final Location predictLocation;
    private final Material predictFluid;

    public FluidPredictor(Location predictLocation, Material predictFluid) {
        this.predictLocation = predictLocation;
        this.predictFluid = predictFluid;
    }

    @Override
    public PredictedLevel predictDamage() {
        int level = predictDamageLevel();

        PredictedLevel predictedLevel = PredictedLevel.checkDamageProbability(level);
        TyroDetector.getPlugin().logToFile("[DEBUG]: 预测流体危害等级：" + level + "，判定程度：" + predictedLevel);
        return predictedLevel;
    }

    @Override
    public int predictDamageLevel() {
        List<Location> loggedLocation = new LocationList();
        List<Location> destroyedBlock = new LocationList();

        checkFlowLocation(predictLocation, false, 8, loggedLocation, destroyedBlock);

        int level = 0;
        for (Location location : destroyedBlock) {
            if (MaterialUtil.isRedstoneBlock(location.getBlock().getType())) {
                level += 4;
            }
            level += 2;
        }
        level += loggedLocation.size();

        for (Location location : loggedLocation) {
            level += checkSurroundings(location);
        }

        return level;
    }

    private boolean checkFlowLocation(Location checkLocation, boolean fall, int level, List<Location> loggedLocation, List<Location> destroyedBlock) {
        if (!MaterialUtil.canReplaceByLiquid(checkLocation.getBlock().getType()) || checkLocation.getY() < 0 || level <= 0 || loggedLocation.contains(checkLocation)) {
            return false;
        }
        loggedLocation.add(checkLocation);

        if (!checkLocation.getBlock().getType().isAir()) {
            destroyedBlock.add(checkLocation);
        }

        int loss = 1;
        if (predictFluid == Material.LAVA && checkLocation.getWorld().getEnvironment() != World.Environment.NETHER) {
            loss = 3;
        }

        boolean willFall = checkFlowLocation(checkLocation.clone().add(0, -1, 0), true, 8, loggedLocation, destroyedBlock);
        if (!(willFall && fall)) {
            // x+
            checkFlowLocation(checkLocation.clone().add(1, 0, 0), fall, level - loss, loggedLocation, destroyedBlock);
            // x-
            checkFlowLocation(checkLocation.clone().add(-1, 0, 0), fall, level - loss, loggedLocation, destroyedBlock);
            // z+
            checkFlowLocation(checkLocation.clone().add(0, 0, 1), fall, level - loss, loggedLocation, destroyedBlock);
            // z-
            checkFlowLocation(checkLocation.clone().add(0, 0, -1), fall, level - loss, loggedLocation, destroyedBlock);
        }

        return true;
    }

    private int checkSurroundings(Location location) {
        boolean wouldFlame = Material.LAVA == this.predictFluid;
        int predictLevel = 0;

        // 检测5x5内的方块
        if (wouldFlame) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        Location checkLocation = location.clone().add(x, y, z);
                        if (checkLocation.getBlock().getType() == Material.TNT) {
                            predictLevel += new TntPredictor(checkLocation).predictDamageLevel();
                        } else if (checkLocation.getBlock().getType().isFlammable()) {
                            predictLevel += new FirePredictor(checkLocation).predictDamageLevel();
                        }
                    }
                }
            }
        }

        return predictLevel;
    }

    @Override
    public String toEmailContent(HumanEntity player, PredictedLevel cache) {
        String loc = "(X:" + predictLocation.getBlockX() + ",Z:" + predictLocation.getBlockZ() + ",Y:" + predictLocation.getBlockY() + ")";
        return player.getWorld().getName() +
                " 放置 " + predictFluid.name() +
                " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                " " + loc +
                " 严重性 " + cache;
    }
}
