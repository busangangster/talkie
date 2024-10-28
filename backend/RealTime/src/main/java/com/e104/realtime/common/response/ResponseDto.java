package com.e104.realtime.common.response;

import com.e104.realtime.common.status.StatusCode;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class ResponseDto {

    private final Object data;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int httpStatus;
    private final int serviceStatus;

    public ResponseDto(StatusCode statusCode, Object data) {
        this.httpStatus = statusCode.getHttpStatus().value();
        this.serviceStatus = statusCode.getServiceStatus();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    public static ResponseEntity<ResponseDto> response(StatusCode statusCode, Object data) {
        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(new ResponseDto(statusCode, data));
    }
}
