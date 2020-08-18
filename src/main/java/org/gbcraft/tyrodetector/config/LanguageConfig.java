package org.gbcraft.tyrodetector.config;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class LanguageConfig {
    private static Map<String, String> map;

    public static void init() {
        map = ConfigReader.getLanguage();
    }

    public static String getName(Material material) {
        String type = material.name();
        return getName(type);
    }

    public static String getName(EntityType entityType) {
        String type = entityType.name();
        return getName(type);
    }

    private static String getName(String type) {
        String res = map.get(type);
        if (StringUtils.isBlank(res)) {
            res = type;
        }
        return res;
    }
}
