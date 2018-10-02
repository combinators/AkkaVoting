package edu.wpi.voting;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import edu.wpi.voting.messages.StartElection;
import edu.wpi.voting.services.Management;
import edu.wpi.voting.services.Voter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    enum Party {
        Java("Java4Ever"),
        Python("Pythonists"),
        C("C-Lovers"),
        NonProgrammers("Marketing People Party");

        public final String partyName;
        Party(String partyName) {
            this.partyName = partyName;
        }
    }

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("VotingSystem");
        List<Party> votes =
                Arrays.asList(
                        Party.Java,
                        Party.Python,
                        Party.Python,
                        Party.C,
                        Party.Java,
                        Party.Java,
                        Party.NonProgrammers,
                        Party.NonProgrammers);

        List<ActorRef> voters = new ArrayList<>();
        for (Party vote: votes) {
            voters.add(system.actorOf(Voter.props(vote.partyName)));
        }
        List<String> eligibleParties = new ArrayList<>();
        for (Party party : Party.values()) {
            if (!party.equals(Party.NonProgrammers)) {
                eligibleParties.add(party.partyName);
            }
        }
        ActorRef voteManager = system.actorOf(Management.props(eligibleParties, voters));

        try {
            voteManager.tell(new StartElection(), ActorRef.noSender());
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}