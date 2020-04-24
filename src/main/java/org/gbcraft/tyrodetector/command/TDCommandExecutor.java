package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gbcraft.tyrodetector.TyroDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 命令执行器
 */
public class TDCommandExecutor implements TabExecutor {

    public TyroDetector plugin;

    public TDCommandExecutor(TyroDetector plugin) {
        this.plugin = plugin;
    }

    /**
     * 处理输入的命令，尝试反射建立子命令实体类并执行
     * @param sender 命令执行者
     * @param command 执行的命令名称(tyro)
     * @param label 标签
     * @param args 命令参数
     * @return true 不使用默认权限提醒，保证命令体系的安全性
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("tyro.base") && args.length > 0){
            String simpleName = args[0];
            String detectName = simpleName.substring(0,1).toUpperCase();
            if(simpleName.length() > 1){
                detectName = detectName + simpleName.substring(1).toLowerCase();
            }
            String clazz = "org.gbcraft.tyrodetector.command."+detectName+"Command";
            try{
                TDCommand cmd = (TDCommand) Class.forName(clazz).getConstructor(TyroDetector.class, CommandSender.class, String[].class).newInstance(plugin, sender, args);
                cmd.run();
            }
            catch (Exception e){}

        }

        return true;
    }

    String[] subCommand = {"config", "reload", "white"};
    String[] whiteSubCommand = {"add", "remove", "list"};
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        int length = args.length;
        if(length == 1){
            return Arrays.stream(subCommand).filter(p -> p.startsWith(args[0])).collect(Collectors.toList());
        }
        if(length > 1 && args[0].equals("white")){
            if(length == 2) {
                return Arrays.stream(whiteSubCommand).filter(p -> p.startsWith(args[1])).collect(Collectors.toList());
            }
            if(args[1].equals("remove")){
                return Arrays.stream(plugin.getWhiteListConfig().list()).filter(p -> p.startsWith(args[2])).collect(Collectors.toList());
            }
        }

        ArrayList<String> list = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(p -> list.add(p.getName()));
        return Arrays.stream(list.toArray(new String[0])).filter(p -> p.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }
}
