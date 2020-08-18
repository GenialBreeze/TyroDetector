package org.gbcraft.tyrodetector.bean;

import java.util.UUID;

public class Inviter {
    private final UUID leader;
    private final UUID inviter;

    public Inviter(UUID leader, UUID inviter) {
        this.leader = leader;
        this.inviter = inviter;
    }

    public UUID getLeader() {
        return leader;
    }

    public UUID getInviter() {
        return inviter;
    }
}
