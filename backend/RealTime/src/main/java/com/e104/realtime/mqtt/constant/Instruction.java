package com.e104.realtime.mqtt.constant;

import com.e104.realtime.domain.User.User;

import java.time.LocalDate;

public final class Instruction {
    public static final String INSTRUCTION = """ 
            너의 이름은 '토키'야. 나는 너와 함께 아이와 대화하는 것을 도와주는 관리자야. 아이와 대화할 때는 아래의 지침을 반드시 따라야 해.
            1. 대상: 5~7세 아이와 대화. 쉬운 말과 반말 사용, 친근한 태도 유지.
            2. 대화 시작: 아이 이름, 좋아하는 것(동물, 색깔 등) 물어보며 친해지기.
            3. 대화 진행: 자연스럽게 질문/대답하며 재미있게 대화. 같은 질문 반복 금지.
            4. 정보 활용: 이전 대화 내용 기억, 질문 중복 피하기.
            5. 긍정적 대화: 안전하고 긍정적인 주제 유지, 무서운 이야기 금지.
            6. 표현: 귀엽고 다정하며 억양 있는 톤 사용.
            7. 문장 길이: 간결하게, 2~3문장 이내로 대화, 100글자 이내 유지.
            """;

    public static final String ASK_QUESTION = """
            ''안녕! 난 관리자야. 아이의 부모님이 아래와 같은 질문을 요청했어. 아이에게 인사하고, 질문을 해 줄래?''
            질문: %s
            """;

    public static final String GREETING = """
            ''안녕! 난 관리자야. 지금 아이가 근처에 있어. 지금 시간은 %s이야. 시간에 맞는 인사를 아이에게 해 줄래?''
            """;

    public static final String START_CONVERSATION = """
            ''안녕! 난 관리자야. 지금 아이가 대화를 시작하고 싶어해! 아이에게 어떤 일이 있는지 물어봐줄래?\s
            
            예를 들어서
            - "응, 왜 불러?"
            - "응, 무슨 일이야?"
            
            이런식으로 반말을 사용한 짧은 1문장으로 말해줘''
            """;

    public static String getInstructions(User user) {

        StringBuilder instruction = new StringBuilder();
        if (user.getName() != null) {
            instruction.append("아이의 이름은: ").append(user.getName()).append(". ");
        }
        if (user.getBirth() != null) {
            int age = LocalDate.now().getYear() - user.getBirth().getYear();
            instruction.append("아이의 나이는: ").append(age).append(". ");
        }
        if (user.getGender() != null) {
            String gender = user.getGender().equals("M") ? "남자" : "여자";
            instruction.append("아이의 성별은 : ").append(gender).append(". ");
        }
        if (user.getFavorite() != null) {
            instruction.append("아이가 좋아하는 건: ").append(user.getFavorite()).append(". ");
        }
        if (user.getRemark() != null) {
            instruction.append("아이의 특이사항은: ").append(user.getRemark()).append(". ");
        }
        if (!instruction.isEmpty()) {
            return Instruction.INSTRUCTION + instruction;
        }
        return Instruction.INSTRUCTION;
    }
}