package org.gbcraft.tyrodetector.config;

import org.gbcraft.tyrodetector.bean.VHRule;

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
    private final Integer liquidCycle;
    private final Map<String, VHRule> liquidMap;
    private final Integer lavaBucketRange;
    private final Integer fireCycle;
    private final Map<String, Integer> fireMap;
    private final Boolean debug;
    private final Integer whiteCycle;
    private final Boolean tntDupePredicate;
    private final Map<String, Boolean> predict;
    private final Map<String, Integer> predictLevel;
    private final String predictSendOn;
    private final Map<String, Integer> predictLimit;
    private final Integer predictCycle;

    public DetectorConfig() {
        this.tyroHours = Integer.parseInt(ConfigReader.getParam("tyroHours"));
        this.itemMap = ConfigReader.getParamMap("itemMap");
        this.brokenCycle = Integer.parseInt(ConfigReader.getParam("brokenCycle"));
        this.brokenMap = ConfigReader.getParamMap("brokenMap");
        this.placeCycle = Integer.parseInt(ConfigReader.getParam("placeCycle"));
        this.placeMap = ConfigReader.getParamMap("placeMap");
        this.entityCycle = Integer.parseInt(ConfigReader.getParam("entityCycle"));
        this.entityMap = ConfigReader.getParamMap("entityMap");
        this.liquidCycle = Integer.parseInt(ConfigReader.getParam("liquidCycle"));
        this.liquidMap = ConfigReader.getVHMap("liquidMap");
        this.lavaBucketRange = Math.abs(Integer.parseInt(ConfigReader.getParam("lavaBucketRange")));
        this.fireCycle = Integer.parseInt(ConfigReader.getParam("fireCycle"));
        this.fireMap = ConfigReader.getParamMap("fireMap");
        this.debug = Boolean.valueOf(ConfigReader.getParam("debug"));
        this.whiteCycle = Integer.parseInt(ConfigReader.getParam("whiteCycle"));
        this.tntDupePredicate = Boolean.valueOf(ConfigReader.getParam("tntDupePredicate"));
        this.predict = ConfigReader.getParamSwitchMap("predict");
        this.predictLevel = ConfigReader.getParamMap("predict-level");
        this.predictSendOn = ConfigReader.getParam("predict-sendon");
        this.predictLimit = ConfigReader.getParamMap("predict-limit");
        this.predictCycle = Integer.parseInt(ConfigReader.getParam("predict-cycle"));
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

    public Map<String, VHRule> getLiquidMap() {
        return liquidMap;
    }

    public Map<String, Integer> getFireMap() {
        return fireMap;
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

    public Boolean getDebug() {
        return debug;
    }

    public Integer getWhiteCycle() {
        return whiteCycle;
    }

    public Integer getLiquidCycle() {
        return liquidCycle;
    }

    public Integer getFireCycle() {
        return fireCycle;
    }

    public Integer getLavaBucketRange() {
        return lavaBucketRange;
    }

    public Boolean getTntDupePredicate() {
        return tntDupePredicate;
    }

    public Map<String, Boolean> getPredict() {
        return predict;
    }

    public Map<String, Integer> getPredictLevel() {
        return predictLevel;
    }

    public String getPredictSendOn() {
        return predictSendOn;
    }

    public Map<String, Integer> getPredictLimit() {
        return predictLimit;
    }

    public Integer getPredictCycle() {
        return predictCycle;
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
                ", liquidCycle=" + liquidCycle +
                ", liquidMap=" + liquidMap +
                ", lavaBucketRange=" + lavaBucketRange +
                ", fireCycle=" + fireCycle +
                ", fireMap=" + fireMap +
                ", debug=" + debug +
                ", whiteCycle=" + whiteCycle +
                ", tntDupePredicate=" + tntDupePredicate +
                ", predict=" + predict +
                ", predictLevel=" + predictLevel +
                '}';
    }
}
