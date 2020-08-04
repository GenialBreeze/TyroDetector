package org.gbcraft.tyrodetector.prediction;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>ContainerListener</code>不再适用于预测所产生的警告
 */
public class PredictContainer {
    private static final PredictContainer predictContainer = new PredictContainer();

    private final Map<HumanEntity, Map<PredictedLevel, List<Predictor>>> predictorsMap = new HashMap<>();
    private final TyroDetector plugin = TyroDetector.getPlugin();

    public void putPredictor(HumanEntity player, PredictedLevel level, Predictor predictor) {
        if (level == PredictedLevel.NONE) {
            return;
        }

        if (PredictedLevel.valueOf(plugin.getDetectorConfig().getPredictSendOn()).ordinal() <= level.ordinal()) {
            // 还记啥记啊，赶紧报警
            sendEmergencyEmail(player, predictor, level);
            return;
        }

        Map<PredictedLevel, List<Predictor>> predictorMap = predictorsMap.computeIfAbsent(player, (p) -> new HashMap<>());
        List<Predictor> predictorList = predictorMap.computeIfAbsent(level, (lvl) -> new ArrayList<>());
        predictorList.add(predictor);

        if (predictorList.size() > plugin.getDetectorConfig().getPredictLimit().getOrDefault(level.name(), Integer.MAX_VALUE)) {
            sendEmail(player, predictor, level);
        }
    }

    private void sendEmergencyEmail(HumanEntity player, Predictor predictor, PredictedLevel level) {
        // FIXME: 高风险的操作警报仍然有可能会被延迟发送
        EmailManager.getManager().append(player, new EmailInfo(predictor.toEmailContent(player, level)));
    }
    private void sendEmail(HumanEntity player, Predictor predictor, PredictedLevel level) {
        EmailManager.getManager().append(player, new EmailInfo(predictor.toEmailContent(player, level)));
    }

    private void releaseAll() {
        predictorsMap.clear();
    }

    public static PredictContainer getPredictContainer() {
        return predictContainer;
    }

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(predictContainer.plugin, predictContainer::releaseAll, predictContainer.plugin.getDetectorConfig().getPredictCycle(), predictContainer.plugin.getDetectorConfig().getPredictCycle());
    }
}
