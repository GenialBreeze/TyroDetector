package org.gbcraft.tyrodetector.prediction.util;

import org.bukkit.Location;

import java.util.ArrayList;

public class LocationList extends ArrayList<Location> {
    @Override
    public boolean contains(Object o) {
        if (o instanceof Location) {
            Location location = (Location) o;
            for (Location check : this) {
                if (check.getWorld() == location.getWorld() && check.distance(location) < 1) {
                    return true;
                }
            }
        }

        return super.contains(o);
    }
}
