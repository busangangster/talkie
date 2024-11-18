package com.e104.realtime.dto;

import lombok.Data;

@Data
public class QuestionCreateRequest {

    private int userSeq;
    private String content;
}
