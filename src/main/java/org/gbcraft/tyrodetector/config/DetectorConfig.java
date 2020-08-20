package org.gbcraft.tyrodetector.config;

import org.gbcraft.tyrodetector.bean.ItemRule;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认配置文件实例
 */
public class DetectorConfig {
    private final Integer tyroHours;
    private final Map<String, ItemRule> itemMap;
    private final Integer brokenCycle;
    private final Map<String, Integer> brokenMap;
    private final Integer placeCycle;
    private final Map<String, Integer> placeMap;
    private final Integer entityCycle;
    private final Map<String, Integer> entityMap;
    private final Boolean debug;
    private final Integer whiteCycle;
    private final Boolean tntDupePredicate;
    private final Map<String, Boolean> predict;
    private final Map<String, Integer> predictLevel;
    private final String predictSendOn;
    private final Map<String, Integer> predictLimit;
    private final Integer predictCycle;
    private final Boolean freezeOnServe;

    public DetectorConfig() {
        this.tyroHours = Integer.parseInt(ConfigReader.getParam("tyroHours"));

        this.itemMap = buildItemMap(ConfigReader.getParamStringMap("itemMap"));

        this.brokenCycle = Integer.parseInt(ConfigReader.getParam("brokenCycle"));
        this.brokenMap = ConfigReader.getParamIntMap("brokenMap");
        this.placeCycle = Integer.parseInt(ConfigReader.getParam("placeCycle"));
        this.placeMap = ConfigReader.getParamIntMap("placeMap");
        this.entityCycle = Integer.parseInt(ConfigReader.getParam("entityCycle"));
        this.entityMap = ConfigReader.getParamIntMap("entityMap");
        this.debug = Boolean.valueOf(ConfigReader.getParam("debug"));
        this.whiteCycle = Integer.parseInt(ConfigReader.getParam("whiteCycle"));
        this.tntDupePredicate = Boolean.valueOf(ConfigReader.getParam("tntDupePredicate"));
        this.predict = ConfigReader.getParamSwitchMap("predict");
        this.predictLevel = ConfigReader.getParamIntMap("predict-level");
        this.predictSendOn = ConfigReader.getParam("predict-sendon");
        this.predictLimit = ConfigReader.getParamIntMap("predict-limit");
        this.predictCycle = Integer.parseInt(ConfigReader.getParam("predict-cycle"));
        this.freezeOnServe = Boolean.valueOf(ConfigReader.getParam("freeze-on-serve"));
    }

    private Map<String, ItemRule> buildItemMap(Map<String, String> itemMap) {
        Map<String, ItemRule> res = new HashMap<>();
        itemMap.forEach((k, v) -> {
            String reg = "^[0-9]+?$";
            String temp = v.trim();
            int add = Integer.MAX_VALUE;
            int remove = Integer.MAX_VALUE;
            if (temp.matches(reg)) {
                add = Integer.parseInt(v);
                remove = Integer.parseInt(v);
            }
            else {
                String[] split = temp.split(" ");
                for (String s : split) {
                    try {
                        if (s.startsWith("+")) {
                            add = Integer.parseInt(s.substring(1));
                        }
                        else if (s.startsWith("-")) {
                            remove = Integer.parseInt(s.substring(1));
                        }
                    }
                    catch (Exception ignore) {
                    }
                }

            }
            ItemRule rule = new ItemRule(add, remove);
            res.put(k, rule);
        });

        return res;
    }

    public Integer getTyroHours() {
        return tyroHours;
    }

    public Map<String, ItemRule> getItemMap() {
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

    public Boolean getDebug() {
        return debug;
    }

    public Integer getWhiteCycle() {
        return whiteCycle;
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

    public Boolean getFreezeOnServe() {
        return freezeOnServe;
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
                ", tntDupePredicate=" + tntDupePredicate +
                ", predict=" + predict +
                ", predictLevel=" + predictLevel +
                '}';
    }
}
