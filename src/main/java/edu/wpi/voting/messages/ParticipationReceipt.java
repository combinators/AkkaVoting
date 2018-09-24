package edu.wpi.voting.messages;

public class ParticipationReceipt {
    public static enum ParticipationResult {
        SUCCESS, INVALID_PASSWORD
    };
    public final ParticipationResult result;

    public ParticipationReceipt(ParticipationResult result) {
        this.result = result;
    }
}
