package com.e104.realtime.dto;

import lombok.Data;

@Data
public class QuestionUpdateRequest {

    private int userSeq;
    private String content;

}
