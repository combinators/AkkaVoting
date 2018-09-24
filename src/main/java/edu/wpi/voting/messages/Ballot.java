package edu.wpi.voting.messages;

import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ballot {
    public final List<String> eligibleParties;
    public final ActorRef votingInstance;
    public final String password;

    public Ballot(List<String> eligibleParties, ActorRef votingInstance, String password) {
        this.eligibleParties = Collections.unmodifiableList(new ArrayList<String>(eligibleParties));
        this.votingInstance = votingInstance;
        this.password = password;
    }
}
