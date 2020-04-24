package org.gbcraft.tyrodetector.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.config.ConfigWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件生成用GUI监测器
 */
public class ConfigBoxListener implements Listener {
    private final TyroDetector plugin;

    public ConfigBoxListener(TyroDetector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBoxClose(InventoryCloseEvent event){
        InventoryView view = event.getView();
        // 确定当前容器为特定容器
        if(view.getTitle().equalsIgnoreCase("TyroDetector Configuration Box")){
            Inventory configBox = view.getTopInventory();
            ItemStack[] items = compressInventory(configBox.getContents());
            Map<String, Integer> content = new HashMap<>();
            for(ItemStack item : items){
                content.put(item.getType().name(), item.getAmount());
            }
            ConfigWriter.generateExpendConfig(content);
        }
    }

    public static ItemStack[] compressInventory(ItemStack[] items) {
        final ArrayList<ItemStack> compressed = new ArrayList<>();
        for (final ItemStack item : items) {
            if (item != null) {
                boolean found = false;
                //If found, add count
                for (final ItemStack item2 : compressed) {
                    if (item2.isSimilar(item)) {
                        item2.setAmount(item2.getAmount() + item.getAmount());
                        found = true;
                        break;
                    }
                }
                //If not found, clone new item.
                if (!found) {
                    compressed.add(item.clone());
                }
            }
        }
        return compressed.toArray(new ItemStack[0]);
    }

}
