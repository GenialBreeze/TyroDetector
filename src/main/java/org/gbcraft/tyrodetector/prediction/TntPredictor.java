package org.gbcraft.tyrodetector.prediction;


import moe.orangemc.luckyfish.plugincommons.item.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.prediction.util.LocationList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TntPredictor implements Predictor {
    private final Location checkLocation;

    public TntPredictor(Location checkLocation) {
        this.checkLocation = checkLocation;
    }

    @Override
    public PredictedLevel predictDamage() {
        int level = predictDamageLevel();
        PredictedLevel predictedLevel = PredictedLevel.checkDamageProbability(level);
        TyroDetector.getPlugin().logToFile("[DEBUG]: 预测TNT危害等级：" + level + "，判定程度：" + predictedLevel);
        return predictedLevel;
    }

    @Override
    public int predictDamageLevel() {
        try {
            return checkBlockBeingBreaked(checkLocation, new LocationList());
        } catch (StackOverflowError err) {
            return 114514;
        }
    }

    private int checkBlockBeingBreaked(Location checkLocation, List<Location> dejaVu) {
        if (Thread.currentThread().getStackTrace().length >= PredictedLevel.HIGH.getMaxProbability() + 20) {
            return 114514;
        }

        Set<Block> blockSet = new HashSet<>();
        // 避免出现来回检测
        if (dejaVu.contains(checkLocation)) {
            return 0;
        }
        dejaVu.add(checkLocation);
        int damageLevel = 0;

        // 此段代码以模拟mc中的爆炸
        // 代码来自mojang
        // 不要打我
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        double dx = x / 15.0f * 2.0f - 1.0f;
                        double dy = y / 15.0f * 2.0f - 1.0f;
                        double dz = z / 15.0f * 2.0f - 1.0f;
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        dx /= distance;
                        dy /= distance;
                        dz /= distance;
                        // 去除随机性，模拟此次爆炸中最糟情况
                        float modifiedRadius = 4 * 1.3f;
                        double checkX = checkLocation.getX();
                        double checkY = checkLocation.getY();
                        double checkZ = checkLocation.getZ();
                        while (modifiedRadius > 0.0f) {
                            Location blockLocation = new Location(checkLocation.getWorld(), checkX, checkY, checkZ);
                            Block block = blockLocation.getBlock();
                            modifiedRadius -= (block.getType().getBlastResistance() + 0.3f) * 0.3f;
                            if (modifiedRadius > 0.0f) {
                                boolean add = true;
                                for (Block blockLogged : blockSet) {
                                    if (blockLogged.getLocation().distance(blockLocation) < 0.8) {
                                        add = false;
                                    }
                                }
                                if (add && !block.getType().isAir()) {
                                    blockSet.add(block);
                                }
                            }
                            checkX += dx * 0.30000001192092896;
                            checkY += dy * 0.30000001192092896;
                            checkZ += dz * 0.30000001192092896;
                            modifiedRadius -= 0.22500001f;
                        }
                    }
                }
            }
        }

        // 结算破坏情况
        for (Block block : blockSet) {
            Location blockLocation = block.getLocation();
            Material type = blockLocation.getBlock().getType();
            if (MaterialUtil.isRedstoneBlock(type)) {
                if (type == Material.TNT) {
                    try {
                        // 爆炸会扩散，但无法预测tnt实体之间的弹射
                        // 故此类破坏无法预测最糟情况
                        damageLevel += checkBlockBeingBreaked(blockLocation, dejaVu);
                    } catch (StackOverflowError e) {
                        // 看来是tnt太多了
                        return 114514;
                    }
                }
                damageLevel += 4;
            } else {
                damageLevel += 2;
            }
        }

        return damageLevel;
    }

    @Override
    public String toEmailContent(HumanEntity player, PredictedLevel cache) {
        String loc = "(X:" + player.getLocation().getBlockX() + ",Z:" + player.getLocation().getBlockZ() + ",Y:" + player.getLocation().getBlockY() + ")";
        return player.getWorld().getName() +
                " 放置 TNT 于 " +
                " " + new SimpleDateFormat("HH:mm").format(new Date()) +
                " " + loc +
                " 严重性预测 " + cache;
    }
}
