package vn.hust.social.backend.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // ===== SUCCESS =====
    SUCCESS(1000, "Success", HttpStatus.OK),

    // ===== AUTH / USER =====
    EMAIL_ALREADY_REGISTERED(2001, "Email already registered", HttpStatus.BAD_REQUEST),
    DISPLAY_NAME_ALREADY_EXISTED(2002, "Display name already existed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(2003, "Email is not verified", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND(2004, "User not found", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(2005, "Invalid password", HttpStatus.BAD_REQUEST),
    EMAIL_SENDING_FAILED(2006, "Failed to send verification email", HttpStatus.SERVICE_UNAVAILABLE),

    INVALID_OR_EXPIRED_EMAIL_VERIFICATION_TOKEN(2007, "Invalid or expired email verification token", HttpStatus.BAD_REQUEST),

    POST_VIEWER_NOT_FOUND(2008, "Post viewer not found", HttpStatus.NOT_FOUND),

    // ===== POST =====
    CANNOT_VIEW_POST(3001, "User is not allowed to view post", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_POST(3002, "User is not allowed to update post", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_POST(3003, "User is not allowed to delete post", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(3004, "Post not found", HttpStatus.NOT_FOUND),

    // ===== COMMENT =====
    CANNOT_VIEW_COMMENTS(4001, "User is not allowed to view comments of this post", HttpStatus.FORBIDDEN),
    CANNOT_UPDATE_COMMENT(3002, "User is not allowed to update comment", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_COMMENT(3003, "User is not allowed to delete comment", HttpStatus.BAD_REQUEST),

    // ===== FRIENDSHIP =====
    FRIENDSHIP_NOT_FOUND(3004, "Friendship not found", HttpStatus.NOT_FOUND),
    FRIENDSHIP_NOT_ACCEPTED(3005, "Friendship has not been accepted", HttpStatus.BAD_REQUEST),

    FRIENDSHIP_ALREADY_EXISTED(3005, "Friendship already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_THIS_FRIENDSHIP(3006, "User does not belong to this friendship", HttpStatus.FORBIDDEN),

    // ===== MEDIA =====

    // ===== VALIDATION =====
    VALIDATION_ERROR(14001, "Validation error", HttpStatus.BAD_REQUEST),

    // ===== GENERIC =====
    UNKNOWN_ERROR(15001, "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
