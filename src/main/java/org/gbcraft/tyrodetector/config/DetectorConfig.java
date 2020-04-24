package org.gbcraft.tyrodetector.config;

import java.util.Map;

/**
 * 默认配置文件实例
 */
public class DetectorConfig {
    private final Integer tyroHours;
    private final Map<String, Integer> itemMap;
    private final Integer brokenCycle;
    private final Map<String, Integer> brokenMap;
    private final Integer placeCycle;
    private final Map<String, Integer> placeMap;
    private final Integer entityCycle;
    private final Map<String, Integer> entityMap;
    private final Boolean debug;
    private final Integer whiteCycle;

    public DetectorConfig(){
        this.tyroHours = Integer.parseInt(ConfigReader.getParam("tyroHours"));
        this.itemMap = ConfigReader.getParamMap("itemMap");
        this.brokenCycle = Integer.parseInt(ConfigReader.getParam("brokenCycle"));
        this.brokenMap = ConfigReader.getParamMap("brokenMap");
        this.placeCycle = Integer.parseInt(ConfigReader.getParam("placeCycle"));
        this.placeMap = ConfigReader.getParamMap("placeMap");
        this.entityCycle = Integer.parseInt(ConfigReader.getParam("entityCycle"));
        this.entityMap = ConfigReader.getParamMap("entityMap");
        this.debug = Boolean.valueOf(ConfigReader.getParam("debug"));
        this.whiteCycle = Integer.parseInt(ConfigReader.getParam("whiteCycle"));
    }

    public Integer getTyroHours() {
        return tyroHours;
    }

    public Map<String, Integer> getItemMap() {
        return itemMap;
    }

    public Map<String, Integer> getBrokenMap() {
        return brokenMap;
    }

    public Map<String, Integer> getPlaceMap() {
        return placeMap;
    }

    public Map<String, Integer> getEntityMap() {
        return entityMap;
    }

    public Integer getBrokenCycle() {
        return brokenCycle;
    }

    public Integer getPlaceCycle() {
        return placeCycle;
    }

    public Integer getEntityCycle() {
        return entityCycle;
    }

    public Boolean getDebug(){return debug;}

    public Integer getWhiteCycle() {
        return whiteCycle;
    }

    @Override
    public String toString() {
        return "DetectorConfig{" +
                "tyroHours=" + tyroHours +
                ", itemMap=" + itemMap +
                ", brokenCycle=" + brokenCycle +
                ", brokenMap=" + brokenMap +
                ", placeCycle=" + placeCycle +
                ", placeMap=" + placeMap +
                ", entityCycle=" + entityCycle +
                ", entityMap=" + entityMap +
                ", debug=" + debug +
                ", whiteCycle=" + whiteCycle +
                '}';
    }
}
