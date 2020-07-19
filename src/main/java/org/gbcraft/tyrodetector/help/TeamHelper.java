package org.gbcraft.tyrodetector.help;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamHelper {
    private static final Map<UUID, UUID> memToLeaRequests = new HashMap<>();

    public static UUID getLeader(UUID member) {
        return memToLeaRequests.get(member);
    }

    public static UUID popLeader(UUID member) {
        UUID res = memToLeaRequests.get(member);
        if (null != res) {
            memToLeaRequests.remove(member);
        }
        return res;
    }

    public static void setRequests(UUID member, UUID leader) {
        memToLeaRequests.put(member, leader);
    }
}
