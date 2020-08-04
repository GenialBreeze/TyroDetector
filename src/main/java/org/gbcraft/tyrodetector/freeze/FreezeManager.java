package org.gbcraft.tyrodetector.freeze;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;
import org.gbcraft.tyrodetector.help.ChatMessageHelper;

import java.util.UUID;

public class FreezeManager {
    private static final FreezeManager freezeManager = new FreezeManager();
    private final AttributeModifier freezeModifier = new AttributeModifier(UUID.fromString("02ecee33-9298-4785-9c34-10a6785212ed"), "tyrodetector freeze", Double.MIN_VALUE, AttributeModifier.Operation.ADD_NUMBER);

    public void freezePlayer(HumanEntity player) {
        // 冻结玩家
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(freezeModifier);
        player.sendMessage(ChatMessageHelper.getMsg("&c你在玩火，请找Frea_(1376779028)解释吧"));
        EmailManager.getManager().append(player, new EmailInfo("因执行沙皇级别操作而遭到冻结，请及时与之取得联系"));
    }
    public void thawPlayer(HumanEntity player) {
        // 解冻玩家
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(freezeModifier);
        player.sendMessage(ChatMessageHelper.getMsg("&a你已经被解冻，下次请不要这么做了"));
    }

    public static FreezeManager getFreezeManager() {
        return freezeManager;
    }
}
