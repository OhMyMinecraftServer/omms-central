package icu.takeneko.omms.central.network.session;

public class RateExceedException extends RuntimeException {
    public RateExceedException(String reason) {
        super(reason);
    }
}
