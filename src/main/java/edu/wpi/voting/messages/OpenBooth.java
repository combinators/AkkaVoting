package edu.wpi.voting.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenBooth {
    public final List<String> eligibleParties;
    public final int voterCount;

    public OpenBooth(List<String> eligibleParties, int voterCount) {
        this.eligibleParties = Collections.unmodifiableList(new ArrayList<String>(eligibleParties));
        this.voterCount = voterCount;
    }
}
