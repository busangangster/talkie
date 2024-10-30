package com.e104.realtime.redis.mapper;

import com.e104.realtime.domain.entity.ConversationContent;
import com.e104.realtime.redis.hash.Conversation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationMapper {
    @Mapping(target = "isAnswer", source = "talker")
    ConversationContent toConversationContent(Conversation conversation);
}
