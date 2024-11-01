package com.e104.realtime.dto;

import lombok.Data;

import java.util.List;

@Data
public class FastApiWordCloudResponse {
    private List<wordCloud> wordCloud;

    @Data
    public static class wordCloud {
        private String word;
        private int count;

        public wordCloud(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }
}
