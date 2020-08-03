package org.gbcraft.tyrodetector.predication;

public enum PredicatedLevel {
    NONE("无"),   // 人畜无害
    LOW("较低"),    // 可能会造成一些轻微的破坏，但似乎无伤大雅
    NORMAL("中等"), // 可能会造成中等程度的破坏，但仍需确认
    HIGH("高"),   // 危害性较高，可能会造成范围较广的破坏
    SERVE("沙皇");   // 危害极大，需要尽快的人工确认及处理
    private final String levelMessage;
    PredicatedLevel(String levelMessage) {
        this.levelMessage = levelMessage;
    }

    public String getLevelMessage() {
        return levelMessage;
    }

    public static PredicatedLevel checkDamageProbability(int damageProbability) {
        // wait to fill
        if (damageProbability <= 0) {
            return NONE;
        } else if (damageProbability <= 10) {
            return LOW;
        } else if (damageProbability <= 40) {
            return NORMAL;
        } else if (damageProbability <= 140) {
            return HIGH;
        } else {
            return SERVE;
        }
    }
}
