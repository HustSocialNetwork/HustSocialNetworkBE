package vn.hust.social.backend.common.response;

public record ApiResponse<T>(
        int code,
        String message,
        T data) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ResponseCode code, T data) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), data);
    }
}
