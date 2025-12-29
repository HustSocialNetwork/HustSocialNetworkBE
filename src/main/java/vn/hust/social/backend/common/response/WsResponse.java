package vn.hust.social.backend.common.response;

public record WsResponse<T>(
        int code,
        String message,
        T data) {
    public static <T> WsResponse<T> success(T data) {
        return new WsResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> WsResponse<T> error(ResponseCode code) {
        return new WsResponse<>(code.getCode(), code.getMessage(), null);
    }
}
