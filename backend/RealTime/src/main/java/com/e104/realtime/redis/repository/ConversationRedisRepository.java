package com.e104.realtime.redis.repository;

import com.e104.realtime.redis.hash.Conversation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConversationRedisRepository extends CrudRepository<Conversation, Long> {
    List<Conversation> findAllByUserSeq(int userSeq);
    void deleteAllByUserSeq(int userSeq);
}
