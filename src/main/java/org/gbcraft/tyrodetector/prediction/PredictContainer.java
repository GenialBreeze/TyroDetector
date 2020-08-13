package org.gbcraft.tyrodetector.prediction;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;
import org.gbcraft.tyrodetector.freeze.FreezeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictContainer {
    private static final PredictContainer predictContainer = new PredictContainer();

    private final Map<HumanEntity, Map<PredictedLevel, List<Predictor>>> predictorsMap = new HashMap<>();
    private final TyroDetector plugin = TyroDetector.getPlugin();

    public void putPredictor(HumanEntity player, PredictedLevel level, Predictor predictor) {
        if (PredictedLevel.valueOf(plugin.getDetectorConfig().getPredictSendOn()).ordinal() <= level.ordinal()) {
            plugin.logToFile("预测到可能的严重破坏，准备直接发送紧急邮件");
            // 还记啥记啊，赶紧报警
            sendEmergencyEmail(player, predictor, level);
            // 严重程度过高，冻结玩家
            if (level == PredictedLevel.SERVE && TyroDetector.getPlugin().getDetectorConfig().getFreezeOnServe()) {
                FreezeManager.getFreezeManager().freezePlayer(player);
            }
            return;
        }

        // 人畜无害，不作记录
        if (level == PredictedLevel.NONE) {
            return;
        }

        // 记录玩家操作风险预测
        Map<PredictedLevel, List<Predictor>> predictorMap = predictorsMap.computeIfAbsent(player, (p) -> new HashMap<>());
        List<Predictor> predictorList = predictorMap.computeIfAbsent(level, (lvl) -> new ArrayList<>());
        predictorList.add(predictor);

        if (predictorList.size() >= plugin.getDetectorConfig().getPredictLimit().getOrDefault(level.name(), Integer.MAX_VALUE)) {
            plugin.logToFile("预测数据达到上限，准备邮件");
            sendEmail(player, predictor, level);
        }
    }

    private void sendEmergencyEmail(HumanEntity player, Predictor predictor, PredictedLevel level) {
        EmailManager.getManager().urgentAppend(player, new EmailInfo(predictor.toEmailContent(player, level)));
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
