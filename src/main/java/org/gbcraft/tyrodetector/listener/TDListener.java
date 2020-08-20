package org.gbcraft.tyrodetector.listener;

import org.gbcraft.tyrodetector.TyroDetector;

public abstract class TDListener {
    protected final TyroDetector plugin;

    public TDListener(TyroDetector plugin) {
        this.plugin = plugin;
    }
}
