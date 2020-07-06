package org.gbcraft.tyrodetector.listener;

import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ContainerListener<K, V> {
    protected final Map<HumanEntity, Map<K, V>> containers = new ConcurrentHashMap<>();
    protected final TyroDetector plugin;

    public ContainerListener(TyroDetector plugin){
        this.plugin = plugin;
    }

    protected abstract void joinContainers(HumanEntity player, K block, Integer limit);
    protected void releaseAll(){
        containers.forEach((k, v) -> containers.remove(k));
    }
}
