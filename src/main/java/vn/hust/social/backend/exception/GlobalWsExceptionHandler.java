package vn.hust.social.backend.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import vn.hust.social.backend.common.response.WsResponse;
import vn.hust.social.backend.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalWsExceptionHandler {

    @MessageExceptionHandler(WsException.class)
    @SendToUser("/queue/errors")
    public WsResponse<Object> handleWsException(WsException e) {
        return WsResponse.error(e.getCode());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public WsResponse<Object> handleUnexpected(Exception e) {
        log.error("Unexpected WebSocket error", e);
        return WsResponse.error(ResponseCode.UNKNOWN_ERROR);
    }
}
