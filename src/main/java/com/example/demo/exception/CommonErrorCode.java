package com.example.demo.exception;

/**
 * Created by liunanhua on 2018/5/9.
 */
public enum CommonErrorCode implements ErrorCode {

    BAD_REQUEST(400, "请求的参数个数或格式不符合要求"),
    INVALID_ARGUMENT(400, "非法参数"),
    UNAUTHORIZED(401, "无权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "请求的地址不正确"),
    METHOD_NOT_ALLOWED(405, "不支持的HTTP请求方法"),
    NOT_ACCEPTABLE(406, "不接受的请求"),
    CONFLICT(409, "资源冲突"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的Media Type"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    REQUEST_SERVICE_ERROR(500, "请求服务失败"),
    SERVICE_UNAVAILABLE(500, "服务不可用"),
    GATEWAY_TIMEOUT(500, "请求服务超时");

    private int status;

    private String message;

    CommonErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CommonErrorCode fromHttpStatus(int httpStatus) {
        for(CommonErrorCode errorCode : values()) {
            if(errorCode.getStatus() == httpStatus) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }


    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}