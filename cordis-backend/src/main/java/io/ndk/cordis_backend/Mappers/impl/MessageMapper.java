package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@AllArgsConstructor
public class MessageMapper implements Mapper<DirectMessageEntity, MessageResponse> {

    private ModelMapper modelMapper;

    @Override
    public MessageResponse mapTo(DirectMessageEntity directMessageEntity) {
        MessageResponse messageResponse = MessageResponse.builder()
                .id(directMessageEntity.getId())
                .chatId(directMessageEntity.getChatId())
                .sender(directMessageEntity.getSender().getUserName())
                .content(directMessageEntity.getContent())
                .sendAt(directMessageEntity.getTimestamp())
                .build();
        return messageResponse;
    }

    @Override
    public DirectMessageEntity mapFrom(MessageResponse directMessageResponse) {
        return modelMapper.map(directMessageResponse, DirectMessageEntity.class);
    }
}
