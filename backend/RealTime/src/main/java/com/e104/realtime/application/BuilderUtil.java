package com.e104.realtime.application;

import com.e104.realtime.domain.vo.Question;
import com.e104.realtime.dto.QuestionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuilderUtil {

    public Question buildQuestion(QuestionCreateRequest request) {
        return Question.builder()
                .content(request.getContent())
                .build();
    }
}
