package com.e104.realtime.mqtt.dto;

import lombok.Getter;

@Getter
public class OpenAiConversationItemCreateRequest {

    private final String type = "conversation.item.create"; // 이벤트 타입

    private final String previous_item_id = null; // 이전 항목 ID (null일 경우 대화의 끝에 추가)

    private Item item; // 대화에 추가할 항목

    public OpenAiConversationItemCreateRequest(String role, String text) {
        this.item = new Item(role, text);
    }

    // 대화 항목을 나타내는 내부 클래스
    @Getter
    public static class Item {
        private final String type = "message"; // 항목의 타입 (예: "message")
        private String role; // 항목의 역할 (예: "user", "assistant")
        private Content[] content; // 항목의 내용

        public Item(String role, String text) {
            this.role = role;
            this.content = new Content[] {new Content(text, role)};
        }

        // 항목 내용을 나타내는 내부 클래스
        @Getter
        public static class Content {
            private String type = "input_text"; // 콘텐츠의 타입 (예: "input_text")
            private String text; // 실제 메시지 내용

            public Content(String text, String role) {
                this.text = text;
                // assistant 응답 저장 시엔 'text'로 넣어주어야 함.
                // 참고: https://platform.openai.com/docs/api-reference/realtime-client-events/conversation/item/create
                this.type = role.equals("assistant")? "text": "input_text";
            }
        }
    }
}
