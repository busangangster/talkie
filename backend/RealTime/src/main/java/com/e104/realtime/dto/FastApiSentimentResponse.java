package com.e104.realtime.dto;

import lombok.Data;

@Data
public class FastApiSentimentResponse {

    private Predictions predictions;

    @Data
    public static class Predictions {
        private int happyScore;

        private int loveScore;

        private int sadScore;

        private int scaryScore;

        private int angryScore;

        private int amazingScore;
    }
}
