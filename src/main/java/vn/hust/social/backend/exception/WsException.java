package vn.hust.social.backend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.hust.social.backend.common.response.ResponseCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WsException extends RuntimeException {

    private final ResponseCode code;

    public WsException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
