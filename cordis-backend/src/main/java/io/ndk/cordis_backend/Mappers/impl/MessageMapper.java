package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper implements Mapper<DirectMessageEntity, MessageResponse> {

    private ModelMapper modelMapper;

    @Override
    public MessageResponse mapTo(DirectMessageEntity directMessageEntity) {
        return modelMapper.map(directMessageEntity, MessageResponse.class);
    }

    @Override
    public DirectMessageEntity mapFrom(MessageResponse directMessageResponse) {
        return modelMapper.map(directMessageResponse, DirectMessageEntity.class);
    }
}
