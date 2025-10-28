package vn.hust.social.backend.exception;

public class EmailAlreadyRegisteredException extends RuntimeException{
    public EmailAlreadyRegisteredException(String email){
        super("Email already registered: " + email);
    }
}
