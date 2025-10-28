package vn.hust.social.backend.exception;

public class DisplayNameAlreadyExistedException extends RuntimeException {
    public DisplayNameAlreadyExistedException(String displayName) {
        super("Display name already existed: " + displayName);
    }
}
