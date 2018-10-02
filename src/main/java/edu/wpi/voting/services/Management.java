package edu.wpi.voting.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import edu.wpi.voting.messages.Ballot;
import edu.wpi.voting.messages.ElectionResults;
import edu.wpi.voting.messages.OpenStation;
import edu.wpi.voting.messages.StartElection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Management extends AbstractActor {
    private final List<String> parties;
    private final List<ActorRef> voters;
    private final List<ActorRef> votersWithoutBallots;

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public Management(List<String> parties, List<ActorRef> voters) {
        this.parties = Collections.unmodifiableList(new ArrayList<>(parties));
        this.voters = voters;
        this.votersWithoutBallots = new LinkedList<>();
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(StartElection.class, s -> {
                    log.info("Election started");
                    ActorRef votingStation = getContext().actorOf(VotingStation.props(getSelf()));
                    votersWithoutBallots.clear();
                    votersWithoutBallots.addAll(voters);
                    votingStation.tell(new OpenStation(parties, voters.size()), getSelf());
                })
                .match(Ballot.class, ballot -> {
                    if (votersWithoutBallots.size() > 0) {
                        votersWithoutBallots.get(0).tell(ballot, getSelf());
                        votersWithoutBallots.remove(0);
                    }
                })
                .match(ElectionResults.class, results -> {
                    log.info("Election finished");
                    results.results.forEach((party, votes) -> log.info("{} : {}", party, votes));
                    log.info("abstained {}", results.abstained);
                    getContext().stop(self());
                }).build();
    }

    public static Props props(List<String> parties, List<ActorRef> voters) {
        return Props.create(Management.class, () -> new Management(parties, voters));
    }
}
