package vn.hust.social.backend.exception;

import lombok.Data;
import vn.hust.social.backend.common.response.ResponseCode;

@Data
public class ApiException extends RuntimeException {

    private final ResponseCode code;

    public ApiException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
