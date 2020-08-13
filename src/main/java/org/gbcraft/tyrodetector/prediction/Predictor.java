package org.gbcraft.tyrodetector.prediction;

import org.bukkit.entity.HumanEntity;

public interface Predictor {
    PredictedLevel predictDamage();

    int predictDamageLevel();

    String toEmailContent(HumanEntity player, PredictedLevel cache);
}
