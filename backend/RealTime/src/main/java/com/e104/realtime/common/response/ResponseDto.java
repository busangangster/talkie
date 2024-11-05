package com.e104.realtime.common.response;

import com.e104.realtime.common.status.StatusCode;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ResponseDto {

    private final Object data;
    private final String message;
    private final String timestamp; // LocalDateTime -> String 변경
    private final int httpStatus;
    private final int serviceStatus;

    public ResponseDto(StatusCode statusCode, Object data) {
        this.httpStatus = statusCode.getHttpStatus().value();
        this.serviceStatus = statusCode.getServiceStatus();
        this.message = statusCode.getMessage();
        this.data = data;

        // timestamp를 문자열 형식으로 변환하여 설정
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static ResponseEntity<ResponseDto> response(StatusCode statusCode, Object data) {
        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(new ResponseDto(statusCode, data));
    }
}
