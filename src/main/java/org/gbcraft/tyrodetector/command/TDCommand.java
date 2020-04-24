package org.gbcraft.tyrodetector.command;

import org.bukkit.command.CommandSender;
import org.gbcraft.tyrodetector.TyroDetector;

/**
 * 子命令抽象类，子命令必须继承该类
 * 命名规范：子命令名称的首字母大写其余字母小写，后缀为Command 如：
 * ConfigCommand 其调用方法为: /tyro config [args]
 */
public abstract class TDCommand {
    protected TyroDetector plugin;
    protected CommandSender sender;
    protected String[] args;

    public TDCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.args = args;
    }

    protected abstract void run();
}
