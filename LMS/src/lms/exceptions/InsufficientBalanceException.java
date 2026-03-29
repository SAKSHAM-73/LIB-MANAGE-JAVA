package lms.exceptions;

public class InsufficientBalanceException extends LMSException {
    public InsufficientBalanceException(double required, double available) {
        super("INSUF_BAL",
              String.format("Insufficient balance. Required: %.2f, Available: %.2f",
                            required, available));
    }
}
