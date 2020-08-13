package org.gbcraft.tyrodetector.command;

import org.bukkit.command.CommandSender;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.ChatMessageHelper;

public class VersionCommand extends TDCommand {
    public VersionCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender.hasPermission("tyro.base")) {
            sender.sendMessage(ChatMessageHelper.getMsg("&1" + plugin.getDescription().getVersion()));
        }
    }
}
