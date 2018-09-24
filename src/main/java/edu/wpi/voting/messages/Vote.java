package edu.wpi.voting.messages;

import akka.actor.ActorRef;

public class Vote {
    public final String party;
    public final String password;
    public final ActorRef replyTo;

    public Vote(String party, String password, ActorRef replyTo) {
        this.party = party;
        this.password = password;
        this.replyTo = replyTo;
    }
}
