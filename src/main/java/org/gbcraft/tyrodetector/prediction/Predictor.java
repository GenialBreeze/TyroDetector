package org.gbcraft.tyrodetector.prediction;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public abstract class Predictor {
    public abstract PredictedLevel predictDamage();

    public abstract int predictDamageLevel();

    public abstract String toEmailContent(HumanEntity player, PredictedLevel cache);
}
