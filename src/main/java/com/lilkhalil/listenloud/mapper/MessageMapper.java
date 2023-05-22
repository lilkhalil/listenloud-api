package com.lilkhalil.listenloud.mapper;

import org.springframework.stereotype.Component;

import com.lilkhalil.listenloud.dto.MessageDTO;
import com.lilkhalil.listenloud.model.Message;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserMapper userMapper;
    
    public MessageDTO toDto(Message message) {

        return MessageDTO.builder()
            .id(message.getId())
            .sender(userMapper.toDto(message.getSender()))
            .recipient(userMapper.toDto(message.getRecipient()))
            .content(message.getContent())
            .timestamp(message.getTimestamp())
            .isReaded(message.getIsReaded())
            .build();
    }

}
