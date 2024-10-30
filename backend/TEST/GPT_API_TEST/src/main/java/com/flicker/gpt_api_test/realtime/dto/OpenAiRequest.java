package com.flicker.gpt_api_test.realtime.dto;

import lombok.Getter;

import java.util.List;
import java.util.Collections;

@Getter
public class OpenAiRequest {
    private final String type = "response.create"; // 고정된 type 값
    private final OpenAiResponse response;

    // instructions만 동적 입력, 나머지 필드는 고정값으로 초기화
    public OpenAiRequest(String instructions) {
        this.response = new OpenAiResponse(instructions);
    }

    @Getter
    static class OpenAiResponse {
        private final String[] modalities = {"text", "audio"}; // 고정된 modalities 값
        private final String instructions; // 동적으로 입력받는 instructions 값
        private final String voice = "alloy"; // 고정된 voice 값
        private final String output_audio_format = "pcm16"; // 고정된 output_audio_format 값
        private final List<OpenAiTool> tools = Collections.singletonList(OpenAiTool.INSTANCE); // 고정된 도구 정보
        private final String tool_choice = "auto"; // 고정된 tool_choice 값
        private final double temperature = 0.7; // 고정된 temperature 값`
        private final int max_output_tokens = 150; // 고정된 max_output_tokens 값

        public OpenAiResponse(String instructions) {
            this.instructions = instructions; // 생성자에서 입력받은 instructions 설정
        }
    }

    @Getter
    static class OpenAiTool {
        public static final OpenAiTool INSTANCE = new OpenAiTool(); // Singleton 패턴으로 고정된 도구 정보

        private final String type = "function"; // 고정된 type 값
        private final String name = "calculate_sum"; // 고정된 name 값
        private final String description = "Calculates the sum of two numbers."; // 고정된 description 값
        private final ToolParameters parameters = new ToolParameters(); // 고정된 parameters 값

        @Getter
        static class ToolParameters {
            private final String type = "object"; // 고정된 type 값
            private final ToolProperties properties = new ToolProperties(); // 고정된 properties 값
            private final String[] required = {"a", "b"}; // 고정된 required 값

            @Getter
            static class ToolProperties {
                private final Parameter a = new Parameter("number"); // 고정된 a 값
                private final Parameter b = new Parameter("number"); // 고정된 b 값

                @Getter
                static class Parameter {
                    private final String type;

                    public Parameter(String type) {
                        this.type = type;
                    }
                }
            }
        }
    }
}
