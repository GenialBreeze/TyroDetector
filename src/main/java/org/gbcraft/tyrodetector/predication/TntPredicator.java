package org.gbcraft.tyrodetector.predication;

import cn.mcres.luckyfish.plugincommons.utils.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class TntPredicator {
    private final Location checkLocation;

    public TntPredicator(Location checkLocation) {
        this.checkLocation = checkLocation;
    }

    public PredicatedLevel predicateDamage() {
        return PredicatedLevel.checkDamageProbability(predicateDamageLevel());
    }

    public int predicateDamageLevel() {
        return checkBlockBeingBreaked(checkLocation, new ArrayList<>());
    }

    private int checkBlockBeingBreaked(Location checkLocation, List<Location> dejaVu) {
        // 避免出现来回检测
        if (dejaVu.contains(checkLocation)) {
            return 0;
        }
        dejaVu.add(checkLocation);
        int damageLevel = 0;

        // 代码来自mojang
        // 不要打我
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        float modifiedRadius = 4 * 1.3f; // 去除随机，按最坏情况算
                        while (modifiedRadius > 0.0f) {
                            Location loc = checkLocation.clone();

                            float blastResistance = loc.getBlock().getType().getBlastResistance();
                            modifiedRadius -= (blastResistance + 0.3f) * 0.3f;
                            if (modifiedRadius > 0.0f) {
                                Material type = loc.getBlock().getType();
                                if (MaterialUtil.isRedstoneBlock(type)) {
                                    damageLevel += 10;
                                } else {
                                    if (type == Material.TNT) {
                                        try {
                                            damageLevel += checkBlockBeingBreaked(loc, dejaVu);
                                        } catch (StackOverflowError e) {
                                            // 看来是tnt太多了
                                            damageLevel = 114514;
                                        }
                                    }
                                    damageLevel += 1;
                                }
                            }
                            modifiedRadius -= 0.22500001f;
                        }
                    }
                }
            }
        }

        return damageLevel;
    }
}
