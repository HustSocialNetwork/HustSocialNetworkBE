package vn.hust.social.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.hust.social.backend.common.response.ApiResponse;
import vn.hust.social.backend.common.response.ResponseCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ResponseCode code = ex.getCode();
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(ResponseCode.VALIDATION_ERROR.getHttpStatus())
                .body(ApiResponse.error(ResponseCode.VALIDATION_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleUnexpected(Exception e) {
        log.error("Unexpected error", e); // optional
        return ResponseEntity
                .status(ResponseCode.UNKNOWN_ERROR.getHttpStatus())
                .body(ApiResponse.error(ResponseCode.UNKNOWN_ERROR, e.getMessage()));
    }
}
