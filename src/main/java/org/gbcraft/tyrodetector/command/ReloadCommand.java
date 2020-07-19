package org.gbcraft.tyrodetector.command;

import org.bukkit.command.CommandSender;
import org.gbcraft.tyrodetector.TyroDetector;

/**
 * 配置文件重载命令，重载除白名单{@link TyroDetector#rebuildConfigInstance}外的配置文件
 */
public class ReloadCommand extends TDCommand {
    public ReloadCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        if (sender.hasPermission("tyro.reload")) {
            plugin.rebuildConfigInstance();
            sender.sendMessage("重载完毕");
        }

    }
}
