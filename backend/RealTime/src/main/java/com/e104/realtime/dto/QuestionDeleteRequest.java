package com.e104.realtime.dto;

import lombok.Data;

@Data
public class QuestionDeleteRequest {
    private int userSeq;
    private int questionSeq;
}
