package org.gbcraft.tyrodetector.help;

import org.bukkit.ChatColor;

public class ChatMessageHelper {
    public static String getMsg(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
