package edu.wpi.voting.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import edu.wpi.voting.messages.*;

import java.util.*;

public class VotingStation extends AbstractActor {
    private final ActorRef manager;
    private final Set<String> ballotPasswords;
    private final Map<String, Integer> votesCast;
    private int abstained;

    public VotingStation(ActorRef manager) {
        this.ballotPasswords = new HashSet<String>();
        this.votesCast = new HashMap<String, Integer>();
        this.abstained = 0;
        this.manager = manager;
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(OpenStation.class, ob -> {
                    ballotPasswords.clear();
                    votesCast.clear();
                    abstained = 0;

                    for (String party : ob.eligibleParties) {
                        votesCast.put(party, 0);
                    }
                    for (int i = 0; i < ob.voterCount; ++i) {
                        String password = UUID.randomUUID().toString();
                        ballotPasswords.add(password);
                        manager.tell(new Ballot(ob.eligibleParties, getSelf(), password), getSelf());
                    }
                    if (ob.voterCount <= 0) {
                        manager.tell(new ElectionResults(votesCast, abstained), getSelf());
                        getContext().stop(getSelf());
                    }
                })
                .match(Vote.class, vote -> {
                    if (!ballotPasswords.contains(vote.password)) {
                        vote.replyTo.tell(new ParticipationReceipt(ParticipationReceipt.ParticipationResult.INVALID_PASSWORD), getSelf());
                    } else {
                        ballotPasswords.remove(vote.password);
                        if (votesCast.keySet().contains(vote.party)) {
                            votesCast.put(vote.party, 1 + votesCast.get(vote.party));
                        } else {
                            abstained += 1;
                        }
                        vote.replyTo.tell(new ParticipationReceipt(ParticipationReceipt.ParticipationResult.SUCCESS), getSelf());
                    }
                    if (ballotPasswords.isEmpty()) {
                        manager.tell(new ElectionResults(votesCast, abstained), getSelf());
                        getContext().stop(getSelf());
                    }
                }).build();
    }


    public static Props props(ActorRef manager) {
        return Props.create(VotingStation.class, () -> new VotingStation(manager));
    }
}
