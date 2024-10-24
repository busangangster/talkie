package com.flicker.gpt_api_test.realtime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiSessionUpdateRequest {

    private final String type = "session.update"; // 고정된 type 값

    // 세션 관련 정보를 담고 있는 객체
    private OpenAiSession session;

    // 생성자: 지침(instructions) 값을 받아서 세션 객체를 생성
    public OpenAiSessionUpdateRequest(String instructions) {
        this.session = new OpenAiSession(instructions);
    }

    // 세션 정보를 나타내는 클래스
    @Getter
    @AllArgsConstructor
    static class OpenAiSession {
        // 응답 형식(모드): 텍스트와 오디오를 사용
        private final String[] modalities = new String[]{"text", "audio"};

        // 사용자로부터 입력받은 지침 (instructions)
        private String instructions;

        // 음성 톤 또는 스타일 (고정값: alloy)
        private final String voice = "alloy";

        // 입력 오디오 형식 (고정값: pcm16)
        private final String input_audio_format = "pcm16";

        // 출력 오디오 형식 (고정값: pcm16)
        private final String output_audio_format = "pcm16";

        // 입력 오디오 전사(음성 인식 모델) 관련 정보
        private InputAudioTranscription input_audio_transcription;

        // 턴 감지 관련 정보
        private TurnDetection turn_detection;

        // 사용할 도구 목록
        private final List<OpenAiTool> tools;

        // 도구 선택 방식 (고정값: 'auto')
        private final String tool_choice = "auto";

        // 응답 생성 시 사용할 온도 값 (모델의 답변의 다양성 제어, 고정값: 0.7)
        private final double temperature = 0.7;

        // 생성할 응답의 최대 토큰 수 (고정값: inf)
        private final int max_response_output_tokens = 150;

        // 생성자: 사용자로부터 instructions를 받아서 세션을 설정, 입력 오디오 전사 모델과 턴 감지도 설정
        public OpenAiSession(String instructions) {
            this.instructions = instructions;
            this.input_audio_transcription = new InputAudioTranscription(); // whisper-1 모델로 고정
            this.turn_detection = null;
            this.tools = List.of(new OpenAiTool("function", "get_weather",
                    "Get the current weather for a location, tell the user you are fetching the weather.",
                    new OpenAiTool.ToolParameters(
                            "object",
                            new OpenAiTool.ToolParameters.ToolProperties(
                                    new OpenAiTool.ToolParameters.ToolProperties.Parameter("string")
                            ),
                            new String[]{"location"}
                    )
            ));
        }
    }

    // 입력 오디오 전사 모델 클래스: whisper-1 모델을 사용
    @Getter
    static class InputAudioTranscription {
        private final String model = "whisper-1"; // 고정된 음성 인식 모델
    }

    @Getter
    @AllArgsConstructor
    static class TurnDetection {
        // 턴 감지 방식: "server_vad"로 고정되어 있으며, VAD(Voice Activity Detection) 서버 측에서 수행
        private final String type = "server_vad";

        // 음성 활동을 감지할 때의 임계값: 0.5로 고정되어 있으며, 감지 민감도를 나타냄
        private final double threshold = 0.5;

        // 음성 활동이 시작되기 전의 패딩 시간 (밀리초): 음성이 시작되기 전의 시간을 보정하기 위해 사용
        private final int prefix_padding_ms = 300;

        // 음성이 끝난 후의 침묵 지속 시간 (밀리초): 음성이 종료된 후 턴이 끝났다고 간주되는 시간을 설정
        private final int silence_duration_ms = 500;
    }

    // 도구 관련 클래스
    @Getter
    @AllArgsConstructor
    static class OpenAiTool {
        private final String type;
        private final String name;
        private final String description;
        private final ToolParameters parameters;

        @Getter
        @AllArgsConstructor
        static class ToolParameters {
            private final String type;
            private final ToolProperties properties;
            private final String[] required;

            @Getter
            @AllArgsConstructor
            static class ToolProperties {
                private final Parameter location;

                @Getter
                @AllArgsConstructor
                static class Parameter {
                    private final String type;
                }
            }
        }
    }
}
