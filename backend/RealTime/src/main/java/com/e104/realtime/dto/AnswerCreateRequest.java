package com.e104.realtime.dto;

import lombok.Data;

@Data
public class AnswerCreateRequest {

    private int userSeq;
    private int questionSeq;
    private String content;
}
