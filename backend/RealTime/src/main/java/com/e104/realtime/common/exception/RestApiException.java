package com.e104.realtime.common.exception;


import com.e104.realtime.common.status.StatusCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {
    private final StatusCode statusCode;
    private final Object data;

    public RestApiException(final StatusCode statusCode, final Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public RestApiException(final StatusCode statusCode) {
        this(statusCode, null);
    }
}