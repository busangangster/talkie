package com.e104.realtime.redis.repository;

import com.e104.realtime.application.Talker;
import com.e104.realtime.redis.hash.Conversation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ConversationRepositoryImplTest {

    public static final int USER_SEQ = -1;

    @Autowired
    ConversationRepositoryImpl conversationRepository;

    @AfterEach
    void cleanup() {
        conversationRepository.deleteAllByUserSeq(USER_SEQ);
    }

    @DisplayName("레디스에 대화기록을 정상적으로 저장하고 불러오는지 테스트")
    @Test
    void saveTest() {
        Conversation conversation = Conversation.builder().content("테스트").talker(Talker.AI.getValue()).userSeq(USER_SEQ).build();

        conversationRepository.save(conversation);

        List<Conversation> conversations = conversationRepository.findAllByUserSeq(USER_SEQ);

        log.info("불러온 데이터: {}", conversations);

        assertAll(
                () -> assertNotNull(conversations),
                () -> assertEquals(1, conversations.size()),
                () -> assertEquals(conversation.getContent(), conversations.get(0).getContent())
        );
    }

}