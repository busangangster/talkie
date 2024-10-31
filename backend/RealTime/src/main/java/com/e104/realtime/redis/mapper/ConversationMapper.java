package com.e104.realtime.redis.mapper;

import com.e104.realtime.domain.User.ConversationContent;
import com.e104.realtime.redis.hash.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationMapper {
    @Mapping(target = "isAnswer", source = "talker")
    ConversationContent toConversationContent(Conversation conversation);
}
