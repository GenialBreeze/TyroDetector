package org.gbcraft.tyrodetector.help;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class LanguageHelper {
    public static String getName(Material material) {
        return new ItemStack(material).getI18NDisplayName();
    }

    public static String getName(EntityType entityType) {
        return entityType.name().toLowerCase();
    }
}
