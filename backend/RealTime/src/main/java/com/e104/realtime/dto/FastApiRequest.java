package com.e104.realtime.dto;

import lombok.Data;

import java.util.List;

@Data
public class FastApiRequest {

    List<String> textList;

    public FastApiRequest(List<String> textList) {
        this.textList = textList;
    }
}
