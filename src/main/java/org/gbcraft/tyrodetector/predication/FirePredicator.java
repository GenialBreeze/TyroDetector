package org.gbcraft.tyrodetector.predication;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class FirePredicator {
    private final Location checkLocation;

    public FirePredicator(Location checkLocation) {
        this.checkLocation = checkLocation;
    }

    public PredicatedLevel predicateDamage() {
        return PredicatedLevel.checkDamageProbability(predicateDamageLevel());
    }

    public int predicateDamageLevel() {
        return checkBlock(checkLocation, new ArrayList<>());
    }

    private int checkBlock(Location checkLocation, List<Location> dejaVu) {
        if (dejaVu.contains(checkLocation)) {
            return 0;
        }
        dejaVu.add(checkLocation);

        int level = 0;
        if (checkLocation.getBlock().getType() == Material.TNT) {
            level += new TntPredicator(checkLocation).predicateDamageLevel();
        } else if (checkLocation.getBlock().getType().isFlammable()) {
            level = 2;
        }
        for (Location surrounding : getLocationSurrounding(checkLocation)) {
            level += checkBlock(surrounding, dejaVu);
        }

        return level;
    }

    private List<Location> getLocationSurrounding(Location origin) {
        List<Location> surroundings = new ArrayList<>();
        for (BlockFace bf : BlockFace.values()) {
            surroundings.add(origin.clone().add(bf.getDirection()));
        }
        return surroundings;
    }
}
