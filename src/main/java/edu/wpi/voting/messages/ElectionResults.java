package edu.wpi.voting.messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ElectionResults {
    public final Map<String, Integer> results;
    public final int abstained;

    public ElectionResults(Map<String, Integer> results, int abstained) {
        this.results = Collections.unmodifiableMap(new HashMap<String, Integer>(results));
        this.abstained = abstained;
    }
}
