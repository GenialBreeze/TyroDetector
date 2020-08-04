package org.gbcraft.tyrodetector.prediction;

import org.gbcraft.tyrodetector.TyroDetector;

public enum PredictedLevel {
    NONE("无", TyroDetector.getPlugin().getDetectorConfig().getPredictLevel().get("NONE")),   // 人畜无害
    LOW("较低", TyroDetector.getPlugin().getDetectorConfig().getPredictLevel().get("LOW")),    // 可能会造成一些轻微的破坏，但似乎无伤大雅
    NORMAL("中等", TyroDetector.getPlugin().getDetectorConfig().getPredictLevel().get("NORMAL")), // 可能会造成中等程度的破坏，但仍需确认
    HIGH("高", TyroDetector.getPlugin().getDetectorConfig().getPredictLevel().get("HIGH")),   // 危害性较高，可能会造成范围较广的破坏
    SERVE("沙皇", Integer.MAX_VALUE);   // 危害极大，需要尽快的人工确认及处理
    private final String levelMessage;
    private final int maxProbability;
    PredictedLevel(String levelMessage, int maxProbability) {
        this.levelMessage = levelMessage;
        this.maxProbability = maxProbability;
    }

    public String getLevelMessage() {
        return levelMessage;
    }

    @Override
    public String toString() {
        return getLevelMessage();
    }

    public static PredictedLevel checkDamageProbability(int damageProbability) {
        if (damageProbability <= NONE.maxProbability) {
            return NONE;
        } else if (damageProbability <= LOW.maxProbability) {
            return LOW;
        } else if (damageProbability <= NORMAL.maxProbability) {
            return NORMAL;
        } else if (damageProbability <= HIGH.maxProbability) {
            return HIGH;
        } else {
            return SERVE;
        }
    }
}
