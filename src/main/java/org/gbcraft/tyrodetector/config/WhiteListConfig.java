package org.gbcraft.tyrodetector.config;

import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.TimeHelperManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置文件实例
 */
public class WhiteListConfig {
    private final List<String> whiteList;

    public WhiteListConfig() {
        whiteList = ConfigReader.getWhiteList();
    }

    public void append(String username){
        if(!isContain(username)) {
            whiteList.add(username);
            ConfigWriter.setWhiteList(whiteList);
        }
    }

    public void remove(String username){
        if(isContain(username)) {
            whiteList.remove(username);
            ConfigWriter.setWhiteList(whiteList);
        }
    }

    public boolean isContain(String username){
        return whiteList.contains(username);
    }

    public String[] list(){
        return whiteList.toArray(new String[0]);
    }

    public void releaseAll(){
        TyroDetector plugin = TyroDetector.getPlugin();
        List<String> removeList= new ArrayList<>();
        whiteList.forEach(p -> {
            //如果超出时限就删除玩家
            if(TimeHelperManager.getPlayHours(p) > plugin.getDetectorConfig().getTyroHours()){
                removeList.add(p);
            }
        });
        removeList.forEach(this::remove);
    }
}
