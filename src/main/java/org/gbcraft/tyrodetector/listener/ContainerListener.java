package org.gbcraft.tyrodetector.listener;

import org.bukkit.entity.HumanEntity;
import org.gbcraft.tyrodetector.TyroDetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ContainerListener<K, V> extends TDListener {
    protected final Map<HumanEntity, Map<K, V>> containers = new ConcurrentHashMap<>();

    public ContainerListener(TyroDetector plugin) {
        super(plugin);
    }

    protected abstract void joinContainers(HumanEntity player, K block, Integer limit);

    protected void releaseAll() {
        containers.forEach((k, v) -> containers.remove(k));
    }
}
