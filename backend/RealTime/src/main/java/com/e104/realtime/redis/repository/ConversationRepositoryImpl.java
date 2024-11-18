package com.e104.realtime.redis.repository;

import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConversationRepositoryImpl implements ConversationRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Conversation> findAllByUserSeq(int userSeq) {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        List<String> conversations = listOperations.range(String.valueOf(userSeq), 0, -1);

        if (Objects.isNull(conversations)) {
            return List.of();
        }

        log.info("레디스로부터 불러온 리스트 데이터: {}", conversations);

        return conversations.stream().map(c -> {
            try {
                return objectMapper.readValue(c, Conversation.class);
            } catch (JsonProcessingException e) {
                log.error("레디스에서 JSON 파싱 작업 중 문제가 발생했습니다.", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public void deleteAllByUserSeq(int userSeq) {
        redisTemplate.delete(String.valueOf(userSeq));
    }

    @Override
    public void save(Conversation conversation) {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        try {
            listOperations.rightPush(String.valueOf(conversation.getUserSeq()), objectMapper.writeValueAsString(conversation));
        } catch (JsonProcessingException e) {
            log.error("레디스에 대화를 저장하기 위해 직렬화하는 중 문제가 발생했습니다.", e);
        }
    }
}
