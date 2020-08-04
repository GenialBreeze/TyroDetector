package org.gbcraft.tyrodetector.help;

import java.util.UUID;

public interface TimeHelper {
    Long getPlayHours(UUID uuid);

    Long getPlayHours(String name);
}
