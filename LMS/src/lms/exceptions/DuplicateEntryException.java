package lms.exceptions;

public class DuplicateEntryException extends LMSException {
    public DuplicateEntryException(String entity, String identifier) {
        super("DUPLICATE", entity + " already exists with identifier: " + identifier);
    }
}
