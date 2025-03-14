package io.ndk.cordis_backend.Mappers.impl;

import io.ndk.cordis_backend.Mappers.Mapper;
import io.ndk.cordis_backend.dto.response.MessageResponse;
import io.ndk.cordis_backend.entity.DirectMessageEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DirectMessageMapper implements Mapper<DirectMessageEntity, MessageResponse> {

    private final ModelMapper modelMapper;

    @Override
    public MessageResponse mapTo(DirectMessageEntity directMessageEntity) {
        return modelMapper.map(directMessageEntity, MessageResponse.class);
    }

    @Override
    public DirectMessageEntity mapFrom(MessageResponse messageResponse) {
        return modelMapper.map(messageResponse, DirectMessageEntity.class);
    }
}
