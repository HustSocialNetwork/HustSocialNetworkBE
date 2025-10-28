package vn.hust.social.backend.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException(String email) {
        super("Email not verified:" + email);
    }
}
