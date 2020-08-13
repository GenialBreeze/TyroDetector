package org.gbcraft.tyrodetector.prediction;

import cn.mcres.luckyfish.plugincommons.utils.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.util.LocationList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FluidPredictor implements Predictor {
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
        try {
            List<Location> loggedLocation = new LocationList();
            List<Location> destroyedBlock = new LocationList();

            // 放置源头，进行流体流动预测
            checkFlowLocation(predictLocation, true, 8, loggedLocation, destroyedBlock);

            // 计算流体流动过程中会造成的破坏
            int level = 0;
            for (Location location : destroyedBlock) {
                if (MaterialUtil.isRedstoneBlock(location.getBlock().getType())) {
                    level += 4;
                }
                level += 2;
            }
            level += loggedLocation.size();

            // 预测流体可能造成的物理现象造成的最坏破坏
            for (Location location : loggedLocation) {
                level += checkSurroundings(location);
            }

            return level;
        } catch (StackOverflowError err) {
            return 114514;
        }
    }

    private boolean checkFlowLocation(Location checkLocation, boolean source, int level, List<Location> loggedLocation, List<Location> destroyedBlock) {
        if (Thread.currentThread().getStackTrace().length >= PredictedLevel.HIGH.getMaxProbability() + 20) {
            throw new StackOverflowError("Too much!");
        }

        if (!MaterialUtil.canReplaceByLiquid(checkLocation.getBlock().getType()) || (MaterialUtil.isLiquid(checkLocation.getBlock().getType()) && !source)) {
            return false;
        }
        if (checkLocation.getY() < 0 || level <= 0 || loggedLocation.contains(checkLocation)) {
            return true;
        }
        loggedLocation.add(checkLocation);

        // 预测会摧毁方块
        if (!checkLocation.getBlock().getType().isAir()) {
            destroyedBlock.add(checkLocation);
        }

        // 模拟流体能流动的距离
        int loss = 1;
        if (predictFluid == Material.LAVA && checkLocation.getWorld().getEnvironment() != World.Environment.NETHER) {
            loss = 3;
        }

        // 检测是否为竖直向下
        boolean willFall = checkFlowLocation(checkLocation.clone().add(0, -1, 0), false, 8, loggedLocation, destroyedBlock);
        if (source || (!willFall)) {
            // x+
            checkFlowLocation(checkLocation.clone().add(1, 0, 0), false, level - loss, loggedLocation, destroyedBlock);
            // x-
            checkFlowLocation(checkLocation.clone().add(-1, 0, 0), false, level - loss, loggedLocation, destroyedBlock);
            // z+
            checkFlowLocation(checkLocation.clone().add(0, 0, 1), false, level - loss, loggedLocation, destroyedBlock);
            // z-
            checkFlowLocation(checkLocation.clone().add(0, 0, -1), false, level - loss, loggedLocation, destroyedBlock);
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
                        // 岩浆可能会引燃tnt
                        if (checkLocation.getBlock().getType() == Material.TNT) {
                            predictLevel += new TntPredictor(checkLocation).predictDamageLevel();
                        } else if (checkLocation.getBlock().getType().isFlammable()) { // 同样岩浆会点燃可燃方块
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
                " 于 " + new SimpleDateFormat("HH:mm").format(new Date()) +
                " " + loc +
                " 严重性预测 " + cache;
    }
}
