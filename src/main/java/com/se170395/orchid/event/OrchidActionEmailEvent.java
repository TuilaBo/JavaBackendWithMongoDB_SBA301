package com.se170395.orchid.event;

import java.time.Instant;

public class OrchidActionEmailEvent {

    private final String actorEmail;
    private final String action;
    private final String orchidId;
    private final String orchidName;
    private final Instant occurredAt;

    public OrchidActionEmailEvent(String actorEmail, String action, String orchidId, String orchidName) {
        this.actorEmail = actorEmail;
        this.action = action;
        this.orchidId = orchidId;
        this.orchidName = orchidName;
        this.occurredAt = Instant.now();
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public String getAction() {
        return action;
    }

    public String getOrchidId() {
        return orchidId;
    }

    public String getOrchidName() {
        return orchidName;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}

