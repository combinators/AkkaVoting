package edu.wpi.voting.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import edu.wpi.voting.messages.Ballot;
import edu.wpi.voting.messages.ParticipationReceipt;
import edu.wpi.voting.messages.Vote;

public class Voter extends AbstractActor {
    public final String voteFor;

    public Voter(String voteFor) {
        this.voteFor = voteFor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Ballot.class, ballot -> {
            ballot.votingInstance.tell(new Vote(voteFor, ballot.password, getSelf()), getSelf());
        }).match(ParticipationReceipt.class, recepit -> {
            getContext().stop(getSelf());
        }).build();
    }

    public static Props props(String voteFor) { return Props.create(Voter.class, () -> new Voter(voteFor)); }
}
