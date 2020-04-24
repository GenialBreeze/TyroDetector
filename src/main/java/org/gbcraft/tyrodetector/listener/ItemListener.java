package org.gbcraft.tyrodetector.listener;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.email.EmailInfo;
import org.gbcraft.tyrodetector.email.EmailManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 物品存取监测器
 */
public class ItemListener implements Listener {
    private final Map<HumanEntity, ItemStack[]> containers = new HashMap<>();
    private final TyroDetector plugin;

    public ItemListener(TyroDetector plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        // 判定不在监测范围内的玩家
        if (!plugin.getTyroPlayers().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        // 容器持有者
        InventoryHolder holder = event.getInventory().getHolder();
        // 如果持有者是某个方块或大型箱子
        if (holder instanceof BlockState || holder instanceof DoubleChest) {
            final HumanEntity player = event.getPlayer();
            // 玩家此前的背包
            final ItemStack[] before = containers.get(player);
            if (before != null) {
                // 玩家操作后的背包
                final ItemStack[] after = compressInventory(event.getInventory().getContents());
                // 操作前后物品变化
                final ItemStack[] diff = compareInventories(before, after);
                // 容器大概所在位置
                final Location location = getInventoryHolderLocation(holder);
                if (location != null) {
                    // 监测物品表
                    Map<String, Integer> itemMap = plugin.getDetectorConfig().getItemMap();

                    StringBuilder contentBuilder = new StringBuilder();

                    for (final ItemStack item : diff) {
                        Integer limit = itemMap.get(item.getType().name());
                        //DEBUG
                        if (limit != null)
                            plugin.logToFile("[DEBUG]发现需要监控存取的物品");

                        if (limit != null && limit <= Math.abs(item.getAmount())) {
                            //DEBUG
                            plugin.logToFile("[DEBUG]存取个数已达到监测值");
                            String loc = "(X:"+location.getBlockX()+",Z:"+location.getBlockZ()+",Y:"+location.getBlockY()+")";
                            //世界类型
                            contentBuilder.append(player.getWorld().getName());
                            if (item.getAmount() < 0) {
                                contentBuilder.append(" 取出 ");
                            }
                            else {
                                contentBuilder.append(" 存入 ");
                            }
                            contentBuilder.append(item.getType());
                            contentBuilder.append(" x");
                            contentBuilder.append(Math.abs(item.getAmount()));
                            contentBuilder.append(" ").append(new SimpleDateFormat("HH:mm").format(new Date()));
                            contentBuilder.append(" ").append(loc);
                            contentBuilder.append("\n");
                        }
                    }
                    String limitItems = contentBuilder.toString();

                    if (!"".equalsIgnoreCase(limitItems)) {
                        //DEBUG
                        plugin.logToFile("[DEBUG]:大量存取预警 - "+player.getName() +"\n" + limitItems);

                        String content = contentBuilder.toString();
                        EmailManager.getManager().append(player, new EmailInfo(content));
                    }
                }
                containers.remove(player);
            }
        }
    }

    // 确保一定能监听到容器打开操作
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        //不处理不监测玩家
        if (!plugin.getTyroPlayers().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BlockState || holder instanceof DoubleChest) {
            containers.put(event.getPlayer(), compressInventory(event.getInventory().getContents()));
        }

    }

    public static Location getInventoryHolderLocation(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return getInventoryHolderLocation(((DoubleChest) holder).getLeftSide());
        }
        else if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }
        else {
            return null;
        }
    }

    public static ItemStack[] compareInventories(ItemStack[] items1, ItemStack[] items2) {
        final ArrayList<ItemStack> diff = new ArrayList<>();
        for (ItemStack current : items2) {
            diff.add(new ItemStack(current));
        }
        for (ItemStack previous : items1) {
            boolean found = false;
            for (ItemStack current : diff) {
                if (current.isSimilar(previous)) {
                    int newAmount = current.getAmount() - previous.getAmount();
                    if (newAmount == 0) {
                        diff.remove(current);
                    }
                    else {
                        current.setAmount(newAmount);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                ItemStack subtracted = new ItemStack(previous);
                subtracted.setAmount(-subtracted.getAmount());
                diff.add(subtracted);
            }
        }
        return diff.toArray(new ItemStack[0]);
    }

    public static ItemStack[] compressInventory(ItemStack[] items) {
        final ArrayList<ItemStack> compressed = new ArrayList<>();
        for (final ItemStack item : items) {
            if (item != null) {
                boolean found = false;
                for (final ItemStack item2 : compressed) {
                    if (item2.isSimilar(item)) {
                        item2.setAmount(item2.getAmount() + item.getAmount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    compressed.add(item.clone());
                }
            }
        }
        return compressed.toArray(new ItemStack[0]);
    }

}
